package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.dev;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ChannelIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.ChannelOperation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared helper used in dev profile to ensure the Discord text channels required by the bot exist.
 * Whenever channels are created or resolved, in-memory channel identifiers are refreshed so
 * subsequent operations immediately pick up the new IDs.
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevChannelProvisioner {
    private final ChannelIdSet channelIdSet;
    private final ChannelOperation channelOperation;
    private final DevPropertyUpdater propertyUpdater;

    private static final List<ChannelDefinition> CHANNELS = List.of(
            // Administration
            new ChannelDefinition("channel.admin.operation", "admin-operations", "Bot 控制面板互動專用", "DormBot Admin"),
            new ChannelDefinition("channel.auth", "auth-review", "住宿生驗證請求審核", "DormBot Admin"),
            new ChannelDefinition("channel.auth-logger", "auth-logs", "驗證流程紀錄(僅供查核)", "DormBot Admin"),
            new ChannelDefinition("channel.rules", "rules", "宿舍規範產生與公告", "DormBot Admin"),

            // Requests / Leave
            new ChannelDefinition("channel.cadre-button", "cadre-button", "幹部快速操作介面", "Requests"),
            new ChannelDefinition("channel.leave", "leave-requests", "住宿生請假入口", "Requests"),
            new ChannelDefinition("channel.request-leave", "leave-review", "幹部審核請假單", "Requests"),
            new ChannelDefinition("channel.request-leave-cadre", "leave-review-cadre", "宿舍幹部請假專區", "Requests"),

            // Announcement & general info
            new ChannelDefinition("channel.announcement", "announcements", "公告產生 / 對外公告", "Announcements"),
            new ChannelDefinition("channel.public", "public-chat", "住戶互動頻道（測試用）", "Announcements"),
            new ChannelDefinition("channel.lottery", "lottery", "活動抽籤公告", "Announcements"),
            new ChannelDefinition("channel.vote", "votes", "宿舍投票結果公告", "Announcements"),
            new ChannelDefinition("channel.leader", "floor-leaders", "樓長資訊公告", "Announcements"),

            // Repair handling
            new ChannelDefinition("channel.repair", "repair-intake", "報修申請入口與回覆", "Repairs"),
            new ChannelDefinition("channel.repairment.normal", "repair-normal", "一般報修單彙整", "Repairs"),
            new ChannelDefinition("channel.repairment.vending", "repair-vending", "販賣機報修", "Repairs"),
            new ChannelDefinition("channel.repairment.machine", "repair-machines", "洗烘衣機報修", "Repairs"),
            new ChannelDefinition("channel.repairment.water", "repair-water", "飲水機/飲水設備報修", "Repairs"),

            // Took coin
            new ChannelDefinition("channel.took-coin", "took-coin", "投幣機取幣/登記", "Took Coin"),
            new ChannelDefinition("channel.took-coin-cadre", "took-coin-cadre", "幹部取幣登記", "Took Coin"),
            new ChannelDefinition("channel.took-coin-get-back-cadre", "took-coin-return", "幹部取幣回收", "Took Coin"),
            new ChannelDefinition("channel.return-by-firm", "took-coin-by-firm", "廠商收回紀錄", "Took Coin"),

            // Floor specific
            new ChannelDefinition("channel.floor-one", "floor-1", "一樓點名/公告", "Floors"),
            new ChannelDefinition("channel.floor-two", "floor-2", "二樓點名/公告", "Floors"),
            new ChannelDefinition("channel.floor-three", "floor-3", "三樓點名/公告", "Floors"),
            new ChannelDefinition("channel.floor-four", "floor-4", "四樓點名/公告", "Floors"),
            new ChannelDefinition("channel.floor-five", "floor-5", "五樓點名/公告", "Floors"),
            new ChannelDefinition("channel.floor-six", "floor-6", "六樓點名/公告", "Floors")
    );

    /**
     * Ensures channels exist for the given guild, creating any missing ones and refreshing cached IDs.
     *
     * @param guild active guild
     * @return provisioning report summarising actions taken
     */
    public ProvisionReport provision(Guild guild) {
        Map<String, Category> categoryCache = new HashMap<>();
        List<String> createdCategories = new ArrayList<>();
        List<CreatedChannelInfo> createdChannels = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        for (ChannelDefinition definition : CHANNELS) {
            TextChannel channel = resolveChannel(guild, definition, warnings);
            if (channel == null) {
                Category category = ensureCategory(guild, definition.categoryName(), categoryCache, createdCategories);
                channel = createChannel(guild, definition, category);
                if (channel != null) {
                    createdChannels.add(new CreatedChannelInfo(definition.propertyKey(), channel));
                }
            }
            if (channel == null) {
                continue;
            }
            channelIdSet.overrideChannelId(definition.propertyKey(), channel.getId());
            propertyUpdater.updateIfBlank(definition.propertyKey(), channel.getId());
        }

        if (!createdChannels.isEmpty() || !createdCategories.isEmpty()) {
            channelOperation.refreshFloorChannelMap();
        }

        if (!createdCategories.isEmpty()) {
            log.info("[DevChannelProvisioner] Created categories: {}", createdCategories);
        }
        if (!createdChannels.isEmpty()) {
            createdChannels.forEach(info -> log.info(
                    "[DevChannelProvisioner] Created channel '{}' (id={}) for key '{}'",
                    info.channel().getName(), info.channel().getId(), info.propertyKey()
            ));
            log.info("[DevChannelProvisioner] Copy the lines below into discord-dev.properties:");
            createdChannels.stream()
                    .map(info -> "%s=%s".formatted(info.propertyKey(), info.channel().getId()))
                    .forEach(line -> log.info("    {}", line));
        }
        warnings.forEach(message -> log.warn("[DevChannelProvisioner] {}", message));

        return new ProvisionReport(createdCategories, createdChannels, warnings);
    }

    private TextChannel resolveChannel(Guild guild,
                                       ChannelDefinition definition,
                                       List<String> warnings) {
        String configuredId = safeTrim(channelIdSet.findChannelId(definition.propertyKey()));
        if (configuredId != null) {
            TextChannel byId = guild.getTextChannelById(configuredId);
            if (byId != null) {
                return byId;
            }
            warnings.add("Configured channel id %s for key '%s' is missing on guild '%s'."
                    .formatted(configuredId, definition.propertyKey(), guild.getName()));
        }

        List<TextChannel> byName = guild.getTextChannelsByName(definition.defaultName(), true);
        if (!byName.isEmpty()) {
            if (byName.size() > 1) {
                warnings.add("Multiple channels named '%s' found. Using the first one (%s)."
                        .formatted(definition.defaultName(), byName.get(0).getId()));
            }
            return byName.get(0);
        }
        return null;
    }

    private Category ensureCategory(Guild guild,
                                    String categoryName,
                                    Map<String, Category> cache,
                                    List<String> createdCategories) {
        if (categoryName == null || categoryName.isBlank()) {
            return null;
        }
        return cache.computeIfAbsent(categoryName, name -> {
            List<Category> existing = guild.getCategoriesByName(name, true);
            if (!existing.isEmpty()) {
                return existing.get(0);
            }
            Category created = guild.createCategory(name).complete();
            createdCategories.add(created.getName());
            return created;
        });
    }

    private TextChannel createChannel(Guild guild,
                                      ChannelDefinition definition,
                                      Category category) {
        var action = guild.createTextChannel(definition.defaultName());
        if (category != null) {
            action = action.setParent(category);
        }
        if (definition.topic() != null && !definition.topic().isBlank()) {
            action = action.setTopic(definition.topic());
        }
        TextChannel channel = action.complete();
        log.debug("[DevChannelProvisioner] Created channel '{}' (id={})", channel.getName(), channel.getId());
        return channel;
    }

    private String safeTrim(String value) {
        if (value == null) {
            return null;
        }
        return value.isBlank() ? null : value.trim();
    }

    public record ProvisionReport(List<String> createdCategories,
                                  List<CreatedChannelInfo> createdChannels,
                                  List<String> warnings) {
        public boolean hasChanges() {
            return !createdCategories.isEmpty() || !createdChannels.isEmpty();
        }
    }

    public record CreatedChannelInfo(String propertyKey, TextChannel channel) {
    }

    private record ChannelDefinition(String propertyKey,
                                     String defaultName,
                                     String topic,
                                     String categoryName) {
    }
}

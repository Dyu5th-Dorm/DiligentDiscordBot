package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.admin.development;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ButtonIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.dev.DevChannelProvisioner;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.dev.DevChannelProvisioner.CreatedChannelInfo;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.dev.DevChannelProvisioner.ProvisionReport;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.Maintenance;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class GenerateDevChannels extends ListenerAdapter {
    private final ButtonIdSet buttonIdSet;
    private final Maintenance maintenance;
    private final DevChannelProvisioner provisioner;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!buttonIdSet.getGenerateDevChannels().equalsIgnoreCase(event.getButton().getId())) {
            return;
        }

        event.deferReply(true).queue();

        if (!maintenance.isMaintenanceStatus()) {
            event.getHook().sendMessage("請先啟用維護模式後再執行自動建頻道。").setEphemeral(true).queue();
            return;
        }

        Guild guild = event.getGuild();
        if (guild == null) {
            event.getHook().sendMessage("找不到伺服器資料，無法建置頻道。").setEphemeral(true).queue();
            return;
        }

        ProvisionReport report = provisioner.provision(guild);

        if (!report.warnings().isEmpty()) {
            String warningMsg = report.warnings().stream()
                    .map(item -> "⚠ " + item)
                    .collect(Collectors.joining("\n"));
            event.getHook().sendMessage(warningMsg).setEphemeral(true).queue();
            if (!report.hasChanges()) {
                return;
            }
        }

        if (!report.hasChanges()) {
            event.getHook().sendMessage("所有頻道都已存在，不需再建立。")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        StringBuilder builder = new StringBuilder("已自動建立/更新以下頻道：\n");
        if (!report.createdCategories().isEmpty()) {
            builder.append("• 新增分類：")
                    .append(String.join(", ", report.createdCategories()))
                    .append("\n");
        }
        if (!report.createdChannels().isEmpty()) {
            builder.append("• 新增頻道：\n");
            for (CreatedChannelInfo info : report.createdChannels()) {
                builder.append("  - ")
                        .append(info.channel().getName())
                        .append(" (`")
                        .append(info.channel().getId())
                        .append("`)")
                        .append(" ← ")
                        .append(info.propertyKey())
                        .append("\n");
            }
            builder.append("\n請將上述 ID 貼回 discord-dev.properties。");
        }

        event.getHook().sendMessage(builder.toString()).setEphemeral(true).queue();
    }
}

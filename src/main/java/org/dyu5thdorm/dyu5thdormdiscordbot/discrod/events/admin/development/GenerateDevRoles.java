package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.admin.development;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ButtonIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.dev.DevRoleProvisioner;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.dev.DevRoleProvisioner.CreatedRoleInfo;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.dev.DevRoleProvisioner.ProvisionReport;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.Maintenance;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class GenerateDevRoles extends ListenerAdapter {
    private final ButtonIdSet buttonIdSet;
    private final Maintenance maintenance;
    private final DevRoleProvisioner provisioner;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!buttonIdSet.getGenerateDevRoles().equalsIgnoreCase(event.getButton().getId())) {
            return;
        }

        event.deferReply(true).queue();

        if (!maintenance.isMaintenanceStatus()) {
            event.getHook().sendMessage("請先啟用維護模式後再執行自動建身分組。")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Guild guild = event.getGuild();
        if (guild == null) {
            event.getHook().sendMessage("找不到伺服器資料，無法建立身分組。")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        ProvisionReport report = provisioner.provision(guild);

        if (!report.warnings().isEmpty()) {
            String warningMsg = report.warnings().stream()
                    .map(item -> "⚠ " + item)
                    .collect(Collectors.joining("\n"));
            event.getHook().sendMessage(warningMsg)
                    .setEphemeral(true)
                    .queue();
            if (!report.hasChanges()) {
                return;
            }
        }

        if (!report.hasChanges()) {
            event.getHook().sendMessage("所有身分組已存在，不需再建立。")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        StringBuilder builder = new StringBuilder("已自動建立/更新以下身分組：\n");
        var propertyBacked = report.createdRoles().stream()
                .filter(info -> !info.propertyKey().startsWith("role.national."))
                .toList();
        var nationalityBacked = report.createdRoles().stream()
                .filter(info -> info.propertyKey().startsWith("role.national."))
                .toList();

        propertyBacked.forEach(info -> builder.append("  - ")
                .append(info.role().getName())
                .append(" (`")
                .append(info.role().getId())
                .append("`)")
                .append(" ← ")
                .append(info.propertyKey())
                .append("\n"));

        nationalityBacked.forEach(info -> builder.append("  - ")
                .append(info.role().getName())
                .append(" (`")
                .append(info.role().getId())
                .append("`)")
                .append(" ← ")
                .append(info.propertyKey())
                .append(" (已寫入資料庫)")
                .append("\n"));

        if (!propertyBacked.isEmpty()) {
            builder.append("\n請將上述非國籍身分組 ID 貼回 discord-dev.properties。");
        }

        event.getHook().sendMessage(builder.toString())
                .setEphemeral(true)
                .queue();
    }
}

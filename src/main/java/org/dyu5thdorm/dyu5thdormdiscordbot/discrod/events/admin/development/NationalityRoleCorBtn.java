package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.admin.development;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ButtonIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.Maintenance;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.RoleOperation;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.living_record.LivingRecord;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.LivingRecordService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NationalityRoleCorBtn extends ListenerAdapter {
    private final ButtonIdSet buttonIdSet;
    private final LivingRecordService livingRecordService;
    private final RoleOperation roleOperation;
    private final Maintenance maintenance;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!buttonIdSet.getNationalityRoleCorrection().equalsIgnoreCase(event.getButton().getId())) {
            return;
        }

        event.deferReply(true).queue();

        Guild guild = event.getGuild();
        if (guild == null) {
            event.getHook().sendMessage("找不到伺服器").setEphemeral(true).queue();
            return;
        }

        roleOperation.refreshNationalityRoleMap();
        Map<String, String> nationalityMap = roleOperation.getNationalityRoleMap();
        if (nationalityMap.isEmpty()) {
            event.getHook().sendMessage("尚未設定任何國籍身分組，請先透過自動建身分組建立後再試。").setEphemeral(true).queue();
            return;
        }

        List<Member> members = guild.getMembers();

        for (Member member : members) {
            Optional<LivingRecord> livingRecord = livingRecordService.findLivingRecordByDiscordId(member.getUser().getId());
            if (livingRecord.isEmpty()) {
                if (maintenance.isMaintenanceStatus()) {
                    roleOperation.removeAllNationalityRoles(guild, member);
                }
                continue;
            }

            if (livingRecord.get().getStudent() == null) {
                continue;
            }

            String citizenship = livingRecord.get().getStudent().getCitizenship();
            if (citizenship == null || citizenship.isBlank()) {
                if (maintenance.isMaintenanceStatus()) {
                    roleOperation.removeAllNationalityRoles(guild, member);
                }
                continue;
            }

            String expectedRoleId = roleOperation.getRoleIdByCitizenship(citizenship);
            if (expectedRoleId == null) {
                continue;
            }

            Role expectedRole = guild.getRoleById(expectedRoleId);
            if (expectedRole == null) {
                continue;
            }

            boolean alreadyHasRole = member.getRoles().stream()
                    .anyMatch(role -> role.getId().equals(expectedRoleId));

            if (alreadyHasRole) {
                continue;
            }

            roleOperation.removeAllNationalityRoles(guild, member);
            guild.addRoleToMember(member, expectedRole).queue();
            event.getHook().sendMessage(
                    String.format("<@%s> 國籍校正 => <@&%s> (%s)",
                            member.getUser().getId(), expectedRoleId, citizenship)
            ).setEphemeral(true).queue();
        }

        event.getHook().sendMessage("國籍身分組校正完畢").setEphemeral(true).queue();
    }
}

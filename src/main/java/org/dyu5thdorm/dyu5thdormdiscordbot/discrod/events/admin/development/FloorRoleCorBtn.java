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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FloorRoleCorBtn extends ListenerAdapter {
    final
    ButtonIdSet buttonIdSet;
    final
    LivingRecordService livingRecordService;
    final
    RoleOperation roleOperation;
    final
    Maintenance maintenance;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String eventButtonId = event.getButton().getId();
        if (!buttonIdSet.getFloorRoleCorrection().equalsIgnoreCase(eventButtonId)) return;

        event.deferReply().setEphemeral(true).queue();

        Guild thisGuild = event.getGuild();

        if (thisGuild == null) {
            event.getHook().sendMessage("找不到伺服器").setEphemeral(true).queue();
            return;
        }

        List<Member> members = thisGuild.getMembers();

        for (Member member : members) {
            Optional<LivingRecord> record = livingRecordService.findLivingRecordByDiscordId(member.getUser().getId());
            if (record.isEmpty()) {
                if (maintenance.isMaintenanceStatus() && !member.getRoles().isEmpty()) {
                    event.getHook().sendMessage(
                            String.format(
                                    "<@%s> 非本學期住宿生，該樓層身份組已被刪除，請查閱。", member.getUser().getId()
                            )
                    ).setEphemeral(true).queue();
                    roleOperation.removeAllFloorRoles(thisGuild, member);
                }
                continue;
            }

            int floor = roleOperation.getFloorByBedId(record.get().getBed().getBedId());
            String correctRoleId = roleOperation.getRoleIdByFloor(floor);
            Role role = thisGuild.getRoleById(correctRoleId);
            if (role == null) {
                event.getHook()
                     .sendMessage(String.format("找不到樓層角色 <@&%s>，請先建立後再試。", correctRoleId))
                     .setEphemeral(true)
                     .queue();
                continue;
            }
            if (!member.getRoles().contains(role)) {
                roleOperation.removeAllFloorRoles(thisGuild, member);
                thisGuild.addRoleToMember(member, role).queue();
                event.getHook().sendMessage(
                        String.format(
                                "<@%s> 成功校正=> <@&%s>", member.getUser().getId(), correctRoleId
                        )
                ).setEphemeral(true).queue();
            }
        }

        event.getHook().sendMessage("校正完畢").setEphemeral(true).queue();
    }
}

package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.admin;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ButtonIdSet;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminDashboardBuilder {
    private final ButtonIdSet buttonIdSet;

    public void rebuild(TextChannel channel) {
        if (channel == null) {
            return;
        }

        channel.getIterableHistory().complete().forEach(message -> message.delete().queue());

        channel.sendMessage("機器人類別").addActionRow(
                Button.danger(buttonIdSet.getShutdown(), "關機"),
                Button.secondary(buttonIdSet.getMaintenance(), "維修模式"),
                Button.secondary(buttonIdSet.getIpLookup(), "IP LOOKUP")
        ).queue();

        channel.sendMessage("重新產生頻道內容 (1)").addActionRow(
                Button.success(buttonIdSet.getGenerateReqAuth(), "驗證"),
                Button.primary(buttonIdSet.getGenerateRules(), "規章"),
                Button.success(buttonIdSet.getGenerateReqRepair(), "報修"),
                Button.success(buttonIdSet.getGenerateReqTookCoin(), "吃錢"),
                Button.success(buttonIdSet.getGenerateReqCadre(), "特定用戶")
        ).queue();

        channel.sendMessage("重新產生頻道內容 (2)").addActionRow(
                Button.primary(buttonIdSet.getGenerateReqLevCadre(), "點名"),
                Button.primary(buttonIdSet.getGenerateReqLev(), "請假"),
                Button.primary(buttonIdSet.getReturnByFirm(), "廠商退幣")
        ).queue();

        channel.sendMessage("伺服器管理").addActionRow(
                Button.primary(buttonIdSet.getGenerateDevChannels(), "自動建頻道"),
                Button.secondary(buttonIdSet.getGenerateDevRoles(), "自動建身分組"),
                Button.secondary(buttonIdSet.getRefreshAdminOperations(), "重建面板")
        ).queue();

        channel.sendMessage("身份組").addActionRow(
                Button.primary(buttonIdSet.getFloorRoleCorrection(), "校正樓層身分組"),
                Button.secondary(buttonIdSet.getNationalityRoleCorrection(), "校正國籍身分組")
        ).queue();
    }
}

package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.took_coin;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ButtonIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ChannelIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.MenuIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ModalIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.templete.took_coin.embed.TookCoinEmbed;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.templete.took_coin.menu.TookCoinMenu;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.templete.took_coin.modals.TookCoinModal;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.Maintenance;
import org.dyu5thdorm.dyu5thdormdiscordbot.line.LineNotify;
import org.dyu5thdorm.dyu5thdormdiscordbot.line.RepairTokenSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.DiscordLink;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.Student;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.DiscordLinkService;
import org.dyu5thdorm.dyu5thdormdiscordbot.took_coin.TookCoinHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;

@Component
public class TookCoinBtnEvent extends ListenerAdapter {
    @Value("${regexp.money}")
    String moneySyntax;
    @Value("${regexp.floor}")
    String floorSyntax;
    @Value("${regexp.floor-and-area}")
    String floorAreaSyntax;
    @Value("${regexp.date}")
    String dateSyntax;

    final
    ButtonIdSet buttonIdSet;
    final
    MenuIdSet menuIdSet;
    final
    ModalIdSet modalIdSet;
    final
    ChannelIdSet channelIdSet;
    final
    TookCoinMenu menu;
    final
    TookCoinModal tookCoinModal;
    final
    TookCoinHandler tookCoin;
    final
    DiscordLinkService discordLinkService;
    final
    TookCoinEmbed tookCoinEmbed;
    final
    LineNotify lineNotify;
    final
    Maintenance maintenance;

    public TookCoinBtnEvent(ButtonIdSet buttonIdSet, MenuIdSet menuIdSet, ModalIdSet modalIdSet, ChannelIdSet channelIdSet, TookCoinMenu menu, TookCoinModal tookCoinModal, TookCoinHandler tookCoin, DiscordLinkService discordLinkService, TookCoinEmbed tookCoinEmbed, LineNotify lineNotify, Maintenance maintenance) {
        this.buttonIdSet = buttonIdSet;
        this.menuIdSet = menuIdSet;
        this.modalIdSet = modalIdSet;
        this.channelIdSet = channelIdSet;
        this.menu = menu;
        this.tookCoinModal = tookCoinModal;
        this.tookCoin = tookCoin;
        this.discordLinkService = discordLinkService;
        this.tookCoinEmbed = tookCoinEmbed;
        this.lineNotify = lineNotify;
        this.maintenance = maintenance;
    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String eventButtonId = event.getButton().getId();
        if (!buttonIdSet.getTookCoin().equalsIgnoreCase(eventButtonId)) return;
        event.deferReply().setEphemeral(true).queue();
        event.getHook().sendMessageComponents(
                ActionRow.of(
                        menu.getMenu()
                )
        ).setEphemeral(true).queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String eventMenuId = event.getSelectMenu().getId();
        if (!menuIdSet.getTookCoin().equalsIgnoreCase(eventMenuId)) return;
        String selectedOptionId = event.getSelectedOptions().get(0).getValue();
        TookCoinHandler.Type reportType = getTypeByMenuId(selectedOptionId);
        if (reportType == null) {
            return;
        }
        event.replyModal(
                tookCoinModal.getModalByType(reportType)
        ).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String eventModalId = event.getModalId();
        if (!List.of(
                modalIdSet.getTookCoinDryer(),
                modalIdSet.getTookCoinVending(),
                modalIdSet.getTookCoinWashMachine()
        ).contains(eventModalId)) return;

        event.deferReply().setEphemeral(true).queue();

        List<String> args = event.getValues().stream().map(ModalMapping::getAsString).toList();

        boolean isWashOrDry = eventModalId.equalsIgnoreCase(modalIdSet.getTookCoinDryer())
                || eventModalId.equalsIgnoreCase(modalIdSet.getTookCoinWashMachine());

        if (isWashOrDry && !args.get(0).matches(floorAreaSyntax)) {
            event.getHook().sendMessageEmbeds(
                    tookCoinEmbed.getBySyntaxWrong(TookCoinEmbed.SyntaxWrong.FLOOR_AREA).build()
            ).setEphemeral(true).queue();
            return;
        } else if (!isWashOrDry && !args.get(0).matches(floorSyntax)) {
            event.getHook().sendMessageEmbeds(
                    tookCoinEmbed.getBySyntaxWrong(TookCoinEmbed.SyntaxWrong.FLOOR).build()
            ).setEphemeral(true).queue();
            return;
        }

        if (!args.get(2).matches(moneySyntax)) {
            event.getHook().sendMessageEmbeds(
                    tookCoinEmbed.getBySyntaxWrong(TookCoinEmbed.SyntaxWrong.MONEY).build()
            ).setEphemeral(true).queue();
            return;
        }

        if (!args.get(3).matches(dateSyntax)) {
            event.getHook().sendMessageEmbeds(
                    tookCoinEmbed.getBySyntaxWrong(TookCoinEmbed.SyntaxWrong.DATE).build()
            ).setEphemeral(true).queue();
            return;
        }

        DiscordLink discordLink = discordLinkService.findByDiscordId(event.getUser().getId());
        if (discordLink == null) {
            event.getHook().sendMessage("未綁定住宿生身分無法進行此動作！").setEphemeral(true).queue();
            return;
        }

        TookCoinHandler.Type type = getTypeByModalId(eventModalId);

        TookCoinHandler.FailReason r = tookCoin.record(type, args, discordLink.getStudent());
        if (r != TookCoinHandler.FailReason.NONE) {
            event.getHook().sendMessageEmbeds(
                    tookCoinEmbed.getByReason(r).build()
            ).setEphemeral(true).queue();
            return;
        }

        if (!maintenance.isMaintenanceStatus()) {
            sendLineNotify(type, args, discordLink.getStudent());
        }

        event.getHook().sendMessage("""
                # 登記成功！
                退費時間為各學期每個禮拜四 **依通知後領取**。
                退費時間若有變動都會在 <#1019840074772402187> 說明。
                
                > **依照此步驟查看登記紀錄：**
                > <#1148940744854347796> -> 吃錢登記 -> 登記記錄查詢、簽收
                """).setEphemeral(true).queue();

        TextChannel textChannel = event.getJDA().getTextChannelById(channelIdSet.getTookCoinCadre());
        EmbedBuilder embedBuilder = getEmbedBuilder(type, event.getUser().getId(), args, discordLink);
        if (textChannel == null) {
            return;
        }
        textChannel.sendMessageEmbeds(
                embedBuilder.build()
        ).queue();
    }

    @NotNull
    private EmbedBuilder getEmbedBuilder(TookCoinHandler.Type type, String userId, List<String> args, DiscordLink discordLink) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.YELLOW);
        embedBuilder.setTitle("有新的一筆吃錢登記");
        embedBuilder.setDescription(
                String.format("<@%s> 登記資訊如下", userId)
        );
        embedBuilder.addField("樓層區域", args.get(0), true);
        embedBuilder.addField("機器", tookCoinEmbed.getMachineName(type.name()), true);
        embedBuilder.addField("故障情況說明", args.get(1), true);
        embedBuilder.addField("卡幣金額", args.get(2), true);
        embedBuilder.addField("發生時間", tookCoin.getLocalDateTime(args.get(3)).format(
                tookCoinEmbed.getDateTimeFormatter()
        ), true);
        embedBuilder.addField("回報者學號", discordLink.getStudent().getStudentId(), true);
        embedBuilder.addField("回報者姓名", discordLink.getStudent().getName(), true);
        return embedBuilder;
    }

    @SneakyThrows
    void sendLineNotify(TookCoinHandler.Type type, List<String> args, Student student) {
        RepairTokenSet.RepairType repairType = getTypeByTookCoinType(type);
        if (repairType == null) {
            return;
        }
        String sb = "\n有新的一筆吃錢登記如下：\n" +
                "\n樓層區域：" + args.get(0) +
                "\n機器：" + tookCoinEmbed.getMachineName(type.name()) +
                "\n故障情況說明：" + args.get(1) +
                "\n卡幣金額：" + args.get(2) +
                "\n發生時間：" + tookCoin.getLocalDateTime(args.get(3)).format(
                tookCoinEmbed.getDateTimeFormatter()) +
                "\n回報者學號：" + student.getStudentId() +
                "\n回報者姓名：" + student.getName();
        lineNotify.sendMessage(sb, repairType);
    }

    RepairTokenSet.RepairType getTypeByTookCoinType(TookCoinHandler.Type tookCoinType) {
        switch (tookCoinType) {
            case WASH_MACHINE, DRYER -> {
                return RepairTokenSet.RepairType.WASH_AND_DRY_MACHINE;
            }
            case VENDING -> {
                return RepairTokenSet.RepairType.VENDING;
            }
        }

        return null;
    }

    TookCoinHandler.Type getTypeByMenuId(String id) {
        if (menuIdSet.getVendingOption().equalsIgnoreCase(id)) {
            return TookCoinHandler.Type.VENDING;
        } else if (menuIdSet.getDryerOption().equalsIgnoreCase(id)) {
            return TookCoinHandler.Type.DRYER;
        } else if (menuIdSet.getWashingMachineOption().equalsIgnoreCase(id)) {
            return TookCoinHandler.Type.WASH_MACHINE;
        } else return null;
    }

    TookCoinHandler.Type getTypeByModalId(String id) {
        if (modalIdSet.getTookCoinVending().equalsIgnoreCase(id)) {
            return TookCoinHandler.Type.VENDING;
        } else if (modalIdSet.getTookCoinDryer().equalsIgnoreCase(id)) {
            return TookCoinHandler.Type.DRYER;
        } else if (modalIdSet.getTookCoinWashMachine().equalsIgnoreCase(id)) {
            return TookCoinHandler.Type.WASH_MACHINE;
        } else return null;
    }
}

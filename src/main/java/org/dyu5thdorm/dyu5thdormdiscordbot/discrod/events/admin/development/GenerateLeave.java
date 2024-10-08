package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.admin.development;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ButtonIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ChannelIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.ChannelOperation;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.ReqLevOperation;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerateLeave extends ListenerAdapter {
    final
    ButtonIdSet buttonIdSet;
    final
    ChannelIdSet channelIdSet;
    final
    ReqLevOperation rlOp;
    final
    ChannelOperation channelOperation;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!buttonIdSet.getGenerateReqLev().equalsIgnoreCase(event.getButton().getId())) return;
        event.deferReply().setEphemeral(true).queue();

        TextChannel reqLevChannel = event.getJDA().getTextChannelById(channelIdSet.getReqLev());

        if (reqLevChannel == null) {
            return;
        }
        channelOperation.deleteAllMessage(reqLevChannel, 100);

        reqLevChannel.sendMessage(
                        String.format("""
                                # 晚間點名請假\\\\補點名
                                - 點名時段說明：
                                  - **除寒、暑假、國定假日、一般假日(六日)、放假前一天外，上課日皆需點名。**
                                  - 點名於 22:00 開始
                                - 點名模式：
                                  - 樓長至房間內點名
                                - 注意事項：
                                  - 無法到場點名或錯過點名須依規定請假\\\\補點名。
                                  - 連續兩天點名缺席未請假者，將會通知家長。
                                  - __當天點名請假時間為 **%s ~ %s**，逾時系統不受理！__
                                  - 請點下方「晚間點名請假\\\\補點名」開始進行請假流程。
                                """, rlOp.getStartTime(), rlOp.getEndTime())
                )
                .addActionRow(
                        Button.danger(buttonIdSet.getReqForLeave(), "晚間點名請假\\補點名"),
                        Button.primary(buttonIdSet.getGenerateAttendanceRate(), "出席狀態查詢")
                ).queue();

        event.getHook().sendMessage("Done").setEphemeral(true).queue();
    }
}

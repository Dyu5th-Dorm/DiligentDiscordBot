package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.attendance.leave;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ButtonIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.templete.attendace.modals.LeaveModal;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.ReqLevOperation;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.Bed;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.Student;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.living_record.LivingRecord;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.LeaveRecordService;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.LivingRecordService;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.NoCallRollDateService;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@PropertySource("classpath:attendance.properties")
@RequiredArgsConstructor
public class LeaveBtnEvent extends ListenerAdapter {
    final
    ButtonIdSet buttonIdSet;
    final
    NoCallRollDateService noCallRollDateService;
    final
    LeaveRecordService leaveRecordService;
    final
    LivingRecordService livingRecordService;
    final
    LeaveModal leaveModal;
    final
    ReqLevOperation reqLevOperation;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String eventButtonId = event.getButton().getId();
        if (!buttonIdSet.getReqForLeave().equalsIgnoreCase(eventButtonId)) return;

        Optional<LivingRecord> livingRecord = livingRecordService.findLivingRecordByDiscordId(event.getUser().getId());
        if (livingRecord.isEmpty()) {
            event.deferReply().setEphemeral(true).queue();
            event.getHook().sendMessage("""
                    > 非本學期住宿生無法使用此功能。
                    """).setEphemeral(true).queue();
            return;
        }

        LocalDate now = LocalDate.now();
        if (noCallRollDateService.exists(now)) {
            event.deferReply().setEphemeral(true).queue();
            event.getHook().sendMessage("""
                    > 今天無須點名！
                    """).setEphemeral(true).queue();
            return;
        }

        Student student = livingRecord.get().getStudent();
        Bed bed = livingRecord.get().getBed();
        if (leaveRecordService.existsByDate(student, bed, now)) {
            event.deferReply().setEphemeral(true).queue();
            event.getHook().sendMessage("""
                    > 您今天已提交過申請！
                    """).setEphemeral(true).queue();
            return;
        }

        if (reqLevOperation.isIllegalTime()) {
            event.deferReply().setEphemeral(true).queue();
            event.getHook().sendMessage(
                    String.format("""
                    > 操作失敗。操作時間為每天的 %s ~ %s。
                    """, reqLevOperation.getStartTime() , reqLevOperation.getEndTime())
            ).setEphemeral(true).queue();
            return;
        }

        event.replyModal(leaveModal.getModal()).queue();
    }
}

package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.attendance.attendance.out;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.dyu5thdorm.dyu5thdormdiscordbot.attendance.AttendanceHandler;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ButtonIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.attendance.attendance.AttendanceEventUtils;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.templete.attendace.modals.AttendanceOutModal;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class AttendanceOutBtn extends ListenerAdapter {
    final
    ButtonIdSet buttonIdSet;
    final
    AttendanceHandler attendanceHandler;
    final
    AttendanceEventUtils attendanceEventUtils;
    final
    AttendanceOutModal attendanceOutModal;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String eventButtonId = event.getButton().getId();
        if (!buttonIdSet.getAttendanceOut().equalsIgnoreCase(eventButtonId)) return;

        if (attendanceHandler.isAfter(LocalTime.now())) {
            attendanceEventUtils.sendEndTime(event, true);
            return;
        }

        String roomId = attendanceEventUtils.getRoomIdFromMessage(event.getMessage());
        if (roomId == null) {
            event.reply("錯誤，請聯絡開發人員。").setEphemeral(true).queue();
            return;
        }

        Modal modal = attendanceOutModal.getModal(roomId);
        event.replyModal(modal).queue();
    }
}

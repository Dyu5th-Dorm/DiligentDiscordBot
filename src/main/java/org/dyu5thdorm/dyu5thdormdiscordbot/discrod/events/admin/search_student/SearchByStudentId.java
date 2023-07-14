package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.admin.search_student;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.EmbedGenerator;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.DiscordLink;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.LivingRecord;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.DiscordLinkService;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.LivingRecordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@PropertySource("classpath:discord.properties")
public class SearchByStudentId extends ListenerAdapter {
    @Value("${component.button.student-info-by-studentId}")
    String studentIdButtonId;
    @Value("${component.modal.student-info-by-studentId}")
    String modelId;
    @Value("${component.modal.student-info-by-studentId-t}")
    String textInputId;
    final LivingRecordService livingRecordService;
    final DiscordLinkService discordLinkService;
    final EmbedGenerator embedGenerator;

    public SearchByStudentId(LivingRecordService livingRecordService, DiscordLinkService discordLinkService, EmbedGenerator embedGenerator) {
        this.livingRecordService = livingRecordService;
        this.discordLinkService = discordLinkService;
        this.embedGenerator = embedGenerator;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getButton().getId().equals(studentIdButtonId)) return;

        event.replyModal(
                Modal.create(modelId, "以學號查詢住宿生")
                        .addActionRow(
                                TextInput.create(textInputId, "住宿生學號", TextInputStyle.SHORT)
                                        .setMinLength(6)
                                        .setMaxLength(8)
                                        .setRequired(true)
                                        .setPlaceholder("F0000000")
                                        .build()
                        )
                        .build()
        ).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equals(modelId)) return;

        String searchStudentId = event.getValue(textInputId).getAsString();

        Set<LivingRecord> livingRecords = livingRecordService.findAllByStudentIdContains(searchStudentId);

        if (livingRecords.isEmpty()) {
            event.reply("查無結果").setEphemeral(true).queue();
            return;
        }

        if (livingRecords.size() > 10) {
            event.reply("搜尋結果超過十筆，請縮小查詢範圍或尋找開發人員從後台查詢。").setEphemeral(true).queue();
            return;
        }

        for (LivingRecord livingRecord : livingRecords) {
            DiscordLink discordLink = discordLinkService.findByStudentId(
                    livingRecord.getStudent().getStudentId()
            ).orElse(null);

            event.getHook().sendMessageEmbeds(
                    embedGenerator.fromStudentId(livingRecord, discordLink).build()
            ).setEphemeral(true).queue();
        }

        event.reply("查詢成功").setEphemeral(true).queue();
    }
}

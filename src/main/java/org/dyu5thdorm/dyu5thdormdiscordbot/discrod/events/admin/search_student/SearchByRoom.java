package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.admin.search_student;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ButtonIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ModalIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.EmbedGenerator;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.ImageUtils;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.DiscordLink;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.living_record.LivingRecord;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.DiscordLinkService;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.LivingRecordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SearchByRoom extends ListenerAdapter {
    final
    ButtonIdSet buttonIdSet;
    final
    ModalIdSet modalIdSet;
    final
    LivingRecordService livingRecordService;
    final
    DiscordLinkService discordLinkService;
    final
    EmbedGenerator embedGenerator;
    final ImageUtils imageUtils;

    @Value("${regexp.room_id}")
    String roomRegex;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!buttonIdSet.getSearchByBedId().equalsIgnoreCase(event.getButton().getId())) return;

        event.replyModal(
                Modal.create(modalIdSet.getSearchByBI(), "以床號查詢住宿生")
                        .addActionRow(
                                TextInput.create(modalIdSet.getFirstTextInput(), "住宿生房號", TextInputStyle.SHORT)
                                        .setPlaceholder("5126")
                                        .setMinLength(4)
                                        .setMaxLength(4)
                                        .setRequired(true)
                                        .build()
                        )
                        .build()
        ).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equals(modalIdSet.getSearchByBI())) return;

        Optional<ModalMapping> mapping = Optional.ofNullable(
                event.getValue(modalIdSet.getFirstTextInput())
        );

        if (mapping.isEmpty()) {
            event.reply("錯誤！輸入不可為空白字串！").setEphemeral(true).queue();
            return;
        }

        String searchingContent = mapping.get().getAsString();

        if (!searchingContent.matches(roomRegex)) {
            event.reply("輸入的房號格式錯誤").setEphemeral(true).queue();
            return;
        }

        Set<LivingRecord> livingRecords = livingRecordService.findAllByRoomId(searchingContent);

        if (livingRecords.isEmpty()) {
            event.reply(
                    String.format(
                            "> %s 查無結果", searchingContent
                    )
            ).setEphemeral(true).queue();
            return;
        }

        if (livingRecords.size() > 10) {
            event.reply("搜尋結果超過十筆，請縮小查詢範圍或尋找開發人員從後台查詢。").setEphemeral(true).queue();
            return;
        }

        for (LivingRecord livingRecord : livingRecords) {
            if (livingRecord.getStudent() == null) {
                event.getHook().sendMessage(String.format(
                        "%s 空床", livingRecord.getBed().getBedId()
                )).setEphemeral(true).queue();
                continue;
            }

            DiscordLink discordLink = discordLinkService.findByStudentId(
                    livingRecord.getStudent().getStudentId()
            );


            EmbedBuilder embedBuilder = embedGenerator.infoFromRoom(
                    livingRecord, discordLink
            );

            var action = event.getHook().sendMessageEmbeds(embedBuilder.build());
            imageUtils.tryGetStudentImage(livingRecord.getStudent().getStudentId())
                    .ifPresent(action::addFiles);
            action.setEphemeral(true).queue();
        }

        event.reply("查詢完畢").setEphemeral(true).queue();
    }
}

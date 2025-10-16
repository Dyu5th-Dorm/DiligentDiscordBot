package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.admin.search_student;

import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SearchByName extends ListenerAdapter {
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

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String eventButtonId = event.getButton().getId();
        if (!buttonIdSet.getSearchByStudentName().equalsIgnoreCase(eventButtonId)) return;

        event.replyModal(
                Modal.create(modalIdSet.getSearchByN(), "以姓名查詢住宿生")
                        .addActionRow(
                                TextInput.create(modalIdSet.getFirstTextInput(), "住宿生姓名", TextInputStyle.SHORT)
                                        .setRequired(true)
                                        .setPlaceholder("陳威仁")
                                        .build()
                        )
                        .build()
        ).queue();
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equals(modalIdSet.getSearchByN())) return;

        Optional<ModalMapping> mapping = Optional.ofNullable(
                event.getValue(modalIdSet.getFirstTextInput())
        );

        if (mapping.isEmpty()) {
            event.reply("錯誤！輸入值不得為空白字串！").setEphemeral(true).queue();
            return;
        }

        String searchingContent = mapping.get().getAsString();
        Set<LivingRecord> livingRecords = livingRecordService.findAllByNameContains(searchingContent);

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
            DiscordLink discordLink = discordLinkService.findByStudentId(
                    livingRecord.getStudent().getStudentId()
            );

            var action = event.getHook().sendMessageEmbeds(
                    embedGenerator.infoFromName(livingRecord, discordLink).build()
            );
            imageUtils.tryGetStudentImage(livingRecord.getStudent().getStudentId())
                    .ifPresent(action::addFiles);
            action.setEphemeral(true).queue();
        }

        event.reply("查詢成功").setEphemeral(true).queue();
    }
}

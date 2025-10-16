package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.admin.search_student;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ButtonIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.MenuIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.EmbedGenerator;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.ImageUtils;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.living_record.LivingRecord;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.LivingRecordService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class SearchByDiscord extends ListenerAdapter {
    final
    ButtonIdSet buttonIdSet;
    final
    MenuIdSet menuIdSet;
    final
    LivingRecordService livingRecordService;
    final
    EmbedGenerator embedGenerator;
    final ImageUtils imageUtils;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String eventButtonId = event.getButton().getId();
        if (!buttonIdSet.getSearchByDiscordId().equalsIgnoreCase(eventButtonId)) return;
        event.deferReply().setEphemeral(true).queue();
        event.getHook().sendMessage(
                "> 請選擇至少一位，至多四位 Discord 帳號進行查詢"
        ).addActionRow(
                EntitySelectMenu.create(menuIdSet.getInfoByDiscordAccOption(), EntitySelectMenu.SelectTarget.USER)
                        .setPlaceholder("請選擇帳號")
                        .setRequiredRange(1, 4)
                        .build()
        ).setEphemeral(true).queue();
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        String eventMenuId = event.getSelectMenu().getId();
        if (!menuIdSet.getInfoByDiscordAccOption().equalsIgnoreCase(eventMenuId)) return;
        event.deferReply().setEphemeral(true).queue();

        List<User> userLists = event.getMentions().getUsers();

        for (User userList : userLists) {
            String userId = userList.getId();
            Optional<LivingRecord> livingRecord = livingRecordService.findLivingRecordByDiscordId(userId);
            if (livingRecord.isEmpty()) {
                event.getHook().sendMessage(
                        String.format(
                                "> <@%s> 查無結果", userId
                        )
                ).setEphemeral(true).queue();
                continue;
            }
            EmbedBuilder embedBuilder = embedGenerator.infoFromDiscord(livingRecord.get(), userId);
            var action = event.getHook().sendMessage(
                    String.format("<@%s>", userId)
            ).addEmbeds(
                    embedBuilder.build()
            );
            imageUtils.tryGetStudentImage(livingRecord.get().getStudent().getStudentId())
                    .ifPresent(action::addFiles);
            action.setEphemeral(true).queue();
        }
    }
}

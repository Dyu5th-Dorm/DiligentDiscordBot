package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.admin.development;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ButtonIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ChannelIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.admin.AdminDashboardBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshAdminDashboardButton extends ListenerAdapter {
    private final ButtonIdSet buttonIdSet;
    private final ChannelIdSet channelIdSet;
    private final AdminDashboardBuilder adminDashboardBuilder;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!buttonIdSet.getRefreshAdminOperations().equalsIgnoreCase(event.getButton().getId())) {
            return;
        }

        event.deferReply(true).queue();

        var channel = event.getJDA().getTextChannelById(channelIdSet.getAdminOperation());
        adminDashboardBuilder.rebuild(channel);

        event.getHook().sendMessage("已重新建置 admin-operations 面板。")
                .setEphemeral(true)
                .queue();
    }
}

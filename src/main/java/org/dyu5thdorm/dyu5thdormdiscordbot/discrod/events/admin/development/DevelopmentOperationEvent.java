package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.admin.development;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ChannelIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.admin.AdminDashboardBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DevelopmentOperationEvent extends ListenerAdapter {
    final ChannelIdSet channelIdSet;
    final AdminDashboardBuilder adminDashboardBuilder;


    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        var channel = event.getGuild().getTextChannelById(channelIdSet.getAdminOperation());
        adminDashboardBuilder.rebuild(channel);
    }
}

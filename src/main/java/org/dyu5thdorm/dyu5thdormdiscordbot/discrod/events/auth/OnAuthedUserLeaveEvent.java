package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.events.auth;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ChannelIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.DiscordLink;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.DiscordLinkService;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OnAuthedUserLeaveEvent extends ListenerAdapter {
    final
    ChannelIdSet channelIdSet;
    final
    DiscordLinkService discordLinkService;

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        TextChannel textChannel = event.getGuild().getTextChannelById(channelIdSet.getLeave());
        if (textChannel == null) {
            // TODO: HANDLE
            return;
        }

        User user = event.getUser();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setThumbnail(user.getAvatarUrl())
                .setTitle("人員離群通知")
                .setColor(Color.ORANGE);

        Optional<DiscordLink> discordLink = discordLinkService.findByDiscordId(user.getId());

        if (discordLink.isEmpty()) {
            embedBuilder.setDescription(
                    String.format("<@%s> 此成員未綁定住宿生身份", user.getId())
            );
            textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
            return;
        }

        discordLinkService.deleteByDiscordId(discordLink.get().getDiscordId());
        embedBuilder.setDescription(String.format(
                "<@%s> 此帳號綁定學號 `%s` 身份。已將該住宿生綁定帳號資料刪除。",
                discordLink.get().getDiscordId(),
                discordLink.get().getStudent().getStudentId()
        ));

        textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
    }
}

package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.dev;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Automatically provisions required channels on the dev guild the first time the bot becomes ready.
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevChannelBootstrapper extends ListenerAdapter {
    private final DevChannelProvisioner provisioner;
    private final Set<String> bootstrappedGuilds = ConcurrentHashMap.newKeySet();

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        Guild guild = event.getGuild();
        if (!bootstrappedGuilds.add(guild.getId())) {
            return;
        }

        DevChannelProvisioner.ProvisionReport report = provisioner.provision(guild);
        if (report.hasChanges()) {
            log.info("[DevChannelBootstrapper] Provisioned channels for guild '{}'", guild.getName());
        }
    }
}

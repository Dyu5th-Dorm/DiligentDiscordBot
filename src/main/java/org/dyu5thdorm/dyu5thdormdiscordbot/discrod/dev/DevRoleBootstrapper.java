package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.dev;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevRoleBootstrapper extends ListenerAdapter {
    private final DevRoleProvisioner provisioner;
    private final Set<String> processedGuilds = ConcurrentHashMap.newKeySet();

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        if (!processedGuilds.add(event.getGuild().getId())) {
            return;
        }
        DevRoleProvisioner.ProvisionReport report = provisioner.provision(event.getGuild());
        if (report.hasChanges()) {
            log.info("[DevRoleBootstrapper] Provisioned roles for guild '{}'", event.getGuild().getName());
        }
    }
}

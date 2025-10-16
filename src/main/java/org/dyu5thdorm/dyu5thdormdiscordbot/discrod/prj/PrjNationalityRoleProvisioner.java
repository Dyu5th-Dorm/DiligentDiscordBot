package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.prj;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.admin.NationalityRoleManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Profile("prj")
@RequiredArgsConstructor
public class PrjNationalityRoleProvisioner extends ListenerAdapter {

    private final NationalityRoleManager nationalityRoleManager;

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        var result = nationalityRoleManager.provision(event.getGuild(), new ProductionCallbacks());

        if (!result.createdRoles().isEmpty()) {
            result.createdRoles().forEach(info -> log.info(
                    "[PrjNationalityRoleProvisioner] Created role '{}' (id={}) for {}",
                    info.role().getName(), info.role().getId(), info.propertyKey()));
        }

        result.warnings().forEach(message -> log.warn("[PrjNationalityRoleProvisioner] {}", message));
    }

    private final class ProductionCallbacks implements NationalityRoleManager.Callbacks {
        @Override
        public Role createRole(Guild guild,
                               String propertyKey,
                               String roleName,
                               List<String> warnings) {
            Role role = guild.createRole()
                    .setName(roleName)
                    .setMentionable(false)
                    .complete();
            log.debug("[PrjNationalityRoleProvisioner] Created nationality role '{}' (id={})", role.getName(), role.getId());
            return role;
        }

        @Override
        public void afterRoleResolved(String propertyKey, Role role) {
            // Production環境不需更新 properties，僅同步資料庫即可。
        }
    }
}

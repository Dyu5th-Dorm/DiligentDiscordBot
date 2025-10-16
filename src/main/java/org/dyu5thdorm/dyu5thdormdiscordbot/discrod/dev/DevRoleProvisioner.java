package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.dev;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.RoleIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.admin.NationalityRoleManager;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.RoleOperation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevRoleProvisioner {
    private final RoleIdSet roleIdSet;
    private final RoleOperation roleOperation;
    private final DevPropertyUpdater propertyUpdater;
    private final NationalityRoleManager nationalityRoleManager;

    private static final List<RoleDefinition> ROLES = List.of(
            new RoleDefinition("role.manager", "Dorm Manager", Color.decode("#1abc9c"), true),
            new RoleDefinition("role.cadre_leader", "Cadre Leader", Color.decode("#3498db"), true),
            new RoleDefinition("role.cadre", "Cadre", Color.decode("#9b59b6"), true),
            new RoleDefinition("role.dev", "Developer", Color.decode("#f1c40f"), true),
            new RoleDefinition("role.floor_one", "Floor 1", null, false),
            new RoleDefinition("role.floor_two", "Floor 2", null, false),
            new RoleDefinition("role.floor_three", "Floor 3", null, false),
            new RoleDefinition("role.floor_four", "Floor 4", null, false),
            new RoleDefinition("role.floor_five", "Floor 5", null, false),
            new RoleDefinition("role.floor_six", "Floor 6", null, false)
    );

    public ProvisionReport provision(Guild guild) {
        List<CreatedRoleInfo> createdRoles = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        for (RoleDefinition definition : ROLES) {
            Role role = resolveRole(guild, definition, warnings);
            if (role == null) {
                role = createRole(guild, definition);
                if (role != null) {
                    createdRoles.add(new CreatedRoleInfo(definition.propertyKey(), role));
                }
            }
            if (role == null) {
                continue;
            }
            roleIdSet.overrideRoleId(definition.propertyKey(), role.getId());
            propertyUpdater.updateIfBlank(definition.propertyKey(), role.getId());
        }

        if (!createdRoles.isEmpty()) {
            roleOperation.refreshFloorRoleMap();
        }

        var nationalityResult = nationalityRoleManager.provision(guild, new DevNationalityCallbacks());
        nationalityResult.createdRoles().forEach(info -> createdRoles.add(new CreatedRoleInfo(info.propertyKey(), info.role())));
        warnings.addAll(nationalityResult.warnings());

        if (!createdRoles.isEmpty()) {
            createdRoles.forEach(info -> log.info(
                    "[DevRoleProvisioner] Created role '{}' (id={}) for key '{}'",
                    info.role().getName(), info.role().getId(), info.propertyKey()
            ));

            var propertyBacked = createdRoles.stream()
                    .filter(info -> !info.propertyKey().startsWith("role.national."))
                    .toList();
            if (!propertyBacked.isEmpty()) {
                log.info("[DevRoleProvisioner] Copy the lines below into discord-dev.properties:");
                propertyBacked.stream()
                        .map(info -> "%s=%s".formatted(info.propertyKey(), info.role().getId()))
                        .forEach(line -> log.info("    {}", line));
            }

            var nationalityRoles = createdRoles.stream()
                    .filter(info -> info.propertyKey().startsWith("role.national."))
                    .toList();
            if (!nationalityRoles.isEmpty()) {
                log.info("[DevRoleProvisioner] Nationality roles synced (persisted to database): {}",
                        nationalityRoles.stream()
                                .map(info -> "%s(%s)".formatted(info.propertyKey(), info.role().getId()))
                                .toList()
                );
            }
        }

        warnings.forEach(message -> log.warn("[DevRoleProvisioner] {}", message));

        return new ProvisionReport(createdRoles, warnings);
    }

    private Role resolveRole(Guild guild,
                             RoleDefinition definition,
                             List<String> warnings) {
        String configuredId = safeTrim(roleIdSet.findRoleId(definition.propertyKey()));
        if (configuredId != null) {
            Role byId = guild.getRoleById(configuredId);
            if (byId != null) {
                return byId;
            }
            warnings.add("Configured role id %s for key '%s' is missing on guild '%s'."
                    .formatted(configuredId, definition.propertyKey(), guild.getName()));
        }

        List<Role> byName = guild.getRolesByName(definition.defaultName(), true);
        if (!byName.isEmpty()) {
            if (byName.size() > 1) {
                warnings.add("Multiple roles named '%s' found. Using the first one (%s)."
                        .formatted(definition.defaultName(), byName.get(0).getId()));
            }
            return byName.get(0);
        }
        return null;
    }

    private Role createRole(Guild guild, RoleDefinition definition) {
        RoleAction action = guild.createRole()
                .setName(definition.defaultName())
                .setMentionable(definition.mentionable());
        if (definition.color() != null) {
            action = action.setColor(definition.color());
        }
        Role role = action.complete();
        log.debug("[DevRoleProvisioner] Created role '{}' (id={})", role.getName(), role.getId());
        return role;
    }

    private String safeTrim(String value) {
        if (value == null) {
            return null;
        }
        return value.isBlank() ? null : value.trim();
    }

    private Role createNationalityRole(Guild guild, String citizenship) {
        String roleName = "Nationality - " + citizenship;
        Role role = guild.createRole()
                .setName(roleName)
                .setMentionable(false)
                .complete();
        log.debug("[DevRoleProvisioner] Created nationality role '{}' (id={})", role.getName(), role.getId());
        return role;
    }

    private final class DevNationalityCallbacks implements NationalityRoleManager.Callbacks {
        @Override
        public Role createRole(Guild guild, String propertyKey, String roleName, List<String> warnings) {
            String citizenship = propertyKey.substring(propertyKey.lastIndexOf('.') + 1);
            return createNationalityRole(guild, citizenship);
        }

        @Override
        public void afterRoleResolved(String propertyKey, Role role) {
            propertyUpdater.updateIfBlank(propertyKey, role.getId());
        }
    }

    public record ProvisionReport(List<CreatedRoleInfo> createdRoles,
                                  List<String> warnings) {
        public boolean hasChanges() {
            return !createdRoles.isEmpty();
        }
    }

    public record CreatedRoleInfo(String propertyKey, Role role) {
    }

    private record RoleDefinition(String propertyKey,
                                  String defaultName,
                                  Color color,
                                  boolean mentionable) {
    }
}

package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils.RoleOperation;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.nationality_role.NationalityRole;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.LivingRecordService;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.NationalityRoleService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NationalityRoleManager {
    private final LivingRecordService livingRecordService;
    private final NationalityRoleService nationalityRoleService;
    private final RoleOperation roleOperation;

    public Result provision(Guild guild, Callbacks callbacks) {
        List<RoleInfo> createdRoles = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        livingRecordService.findDistinctCitizenshipsForCurrentTerm().forEach(citizenship -> {
            String propertyKey = "role.national." + citizenship;
            String roleName = "Nationality - " + citizenship;

            String storedRoleId = nationalityRoleService.findByCitizenship(citizenship)
                    .map(NationalityRole::getRoleId)
                    .orElse(null);

            Role role = findExistingRole(guild, storedRoleId, roleName, propertyKey, warnings);

            if (role == null) {
                role = callbacks.createRole(guild, propertyKey, roleName, warnings);
                if (role != null) {
                    createdRoles.add(new RoleInfo(propertyKey, role));
                }
            }

            if (role == null) {
                return;
            }

            nationalityRoleService.saveOrUpdate(citizenship, role.getId());
            callbacks.afterRoleResolved(propertyKey, role);
        });

        roleOperation.refreshNationalityRoleMap();
        return new Result(createdRoles, warnings);
    }

    private Role findExistingRole(Guild guild,
                                  String configuredId,
                                  String roleName,
                                  String propertyKey,
                                  List<String> warnings) {
        if (configuredId != null && !configuredId.isBlank()) {
            Role byId = guild.getRoleById(configuredId);
            if (byId != null) {
                return byId;
            }
            warnings.add("Configured role id %s for key '%s' is missing on guild '%s'."
                    .formatted(configuredId, propertyKey, guild.getName()));
        }

        List<Role> byName = guild.getRolesByName(roleName, true);
        if (!byName.isEmpty()) {
            if (byName.size() > 1) {
                warnings.add("Multiple roles named '%s' found. Using the first one (%s)."
                        .formatted(roleName, byName.get(0).getId()));
            }
            return byName.get(0);
        }

        return null;
    }

    public interface Callbacks {
        Role createRole(Guild guild, String propertyKey, String roleName, List<String> warnings);

        void afterRoleResolved(String propertyKey, Role role);
    }

    public record RoleInfo(String propertyKey, Role role) {
    }

    public record Result(List<RoleInfo> createdRoles, List<String> warnings) {
    }
}

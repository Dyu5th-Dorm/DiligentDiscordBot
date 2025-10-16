package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.RoleIdSet;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.NationalityRoleService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoleOperation {
    final RoleIdSet roleIdSet;
    final NationalityRoleService nationalityRoleService;
    Map<Integer, String> floorRoleIdMap;
    Map<String, String> nationalityRoleMap;

    @PostConstruct
    void initRole() {
        refreshFloorRoleMap();
        refreshNationalityRoleMap();
    }

    public int getFloorByBedId(String bedId) {
        char floor = bedId.charAt(1);
        return Character.getNumericValue(floor);
    }

   public void addRoleToMemberByFloor(Guild guild, Member member, String bedId) {
        String roleId = this.getRoleIdByFloor(
                this.getFloorByBedId(bedId)
        );

        Role role = guild.getRoleById(roleId);
        if (role == null) return;
        guild.addRoleToMember(member, role).queue();
    }

    public String getRoleIdByFloor(int floor) {
        return this.floorRoleIdMap.getOrDefault(floor, null);
    }

    public void refreshFloorRoleMap() {
        Map<Integer, String> map = new HashMap<>();
        putIfPresent(map, 1, roleIdSet.getFloorOne());
        putIfPresent(map, 2, roleIdSet.getFloorTwo());
        putIfPresent(map, 3, roleIdSet.getFloorThree());
        putIfPresent(map, 4, roleIdSet.getFloorFour());
        putIfPresent(map, 5, roleIdSet.getFloorFive());
        putIfPresent(map, 6, roleIdSet.getFloorSix());
        floorRoleIdMap = Collections.unmodifiableMap(map);
    }

    private void putIfPresent(Map<Integer, String> target, int floor, String roleId) {
        if (roleId == null || roleId.isBlank()) return;
        target.put(floor, roleId);
    }

    public void refreshNationalityRoleMap() {
        nationalityRoleMap = Collections.unmodifiableMap(
                new HashMap<>(nationalityRoleService.getAllMappings())
        );
    }

    public void addRoleByCitizenship(Guild guild, Member member, String citizenship) {
        if (citizenship == null || citizenship.isBlank()) return;
        if (nationalityRoleMap == null || nationalityRoleMap.isEmpty()) return;
        String roleId = nationalityRoleMap.get(citizenship);
        if (roleId == null || roleId.isBlank()) return;
        Role role = guild.getRoleById(roleId);
        if (role == null) return;
        guild.addRoleToMember(member, role).queue();
    }

    public String getRoleIdByCitizenship(String citizenship) {
        if (citizenship == null || citizenship.isBlank()) return null;
        return getNationalityRoleMap().get(citizenship);
    }

    public Map<String, String> getNationalityRoleMap() {
        return nationalityRoleMap == null ? Collections.emptyMap() : nationalityRoleMap;
    }

    public void removeAllNationalityRoles(Guild guild, Member member) {
        if (member == null || guild == null) return;
        if (getNationalityRoleMap().isEmpty()) return;
        member.getRoles().stream()
                .filter(role -> getNationalityRoleMap().containsValue(role.getId()))
                .forEach(role -> guild.removeRoleFromMember(member, role).queue());
    }

    public void removeAllRoles(Guild guild, Member member) {
        member.getRoles().forEach(
                role -> guild.removeRoleFromMember(member, role).queue()
        );
    }

    public void removeAllFloorRoles(Guild guild, Member member) {
        for (Role role : member.getRoles()) {
            if (!this.floorRoleIdMap.containsValue(role.getId())) continue;
            guild.removeRoleFromMember(member, role).queue();
        }
    }
}

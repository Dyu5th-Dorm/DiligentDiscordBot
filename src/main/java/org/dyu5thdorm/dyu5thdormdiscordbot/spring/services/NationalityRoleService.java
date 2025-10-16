package org.dyu5thdorm.dyu5thdormdiscordbot.spring.services;

import lombok.RequiredArgsConstructor;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.nationality_role.NationalityRole;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.repositories.NationalityRoleRepo;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NationalityRoleService {
    private final NationalityRoleRepo nationalityRoleRepo;

    public Optional<NationalityRole> findByCitizenship(String citizenship) {
        return nationalityRoleRepo.findById(citizenship);
    }

    public NationalityRole saveOrUpdate(String citizenship, String roleId) {
        return nationalityRoleRepo.save(
                NationalityRole.builder()
                        .citizenship(citizenship)
                        .roleId(roleId)
                        .build()
        );
    }

    public Map<String, String> getAllMappings() {
        return nationalityRoleRepo.findAll().stream()
                .collect(Collectors.toUnmodifiableMap(NationalityRole::getCitizenship, NationalityRole::getRoleId));
    }
}

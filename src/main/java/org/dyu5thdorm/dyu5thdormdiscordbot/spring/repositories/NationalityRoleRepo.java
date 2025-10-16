package org.dyu5thdorm.dyu5thdormdiscordbot.spring.repositories;

import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.nationality_role.NationalityRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NationalityRoleRepo extends JpaRepository<NationalityRole, String> {
}

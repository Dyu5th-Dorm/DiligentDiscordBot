package org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.nationality_role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nationality_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NationalityRole {
    @Id
    @Column(name = "citizenship", nullable = false, length = 32)
    private String citizenship;

    @Column(name = "role_id", nullable = false, length = 32)
    private String roleId;
}

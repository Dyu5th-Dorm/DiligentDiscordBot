package org.dyu5thdorm.dyu5thdormdiscordbot.spring.repositories;

import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface StudentRepo extends JpaRepository<Student, String> {
    Set<Student> findAllByStudentId(String studentId);
    Set<Student> findAllByStudentIdContains(String studentId);

    @Query("select distinct s.citizenship from Student s")
    Set<String> findDistinctCitizenships();

}

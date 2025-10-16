package org.dyu5thdorm.dyu5thdormdiscordbot.spring.services;

import lombok.RequiredArgsConstructor;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.Student;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.repositories.StudentRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentService {
    final
    StudentRepo studentRepository;


    public boolean exists(String studentId) {
        return studentRepository.existsById(studentId);
    }

    public void saveStudentPhoneNumber(String studentId, String phoneNumber) {
        Optional<Student> getStudent = studentRepository.findById(studentId);
        getStudent.ifPresent(student -> {
            student.setPhoneNumber(phoneNumber);
            studentRepository.save(student);
        });
    }

    public void saveStudentEmail(String studentId, String email) {
        Optional<Student> getStudent = studentRepository.findById(studentId);
        getStudent.ifPresent(student -> {
            student.setEmail(email);
            studentRepository.save(student);
        });
    }

    public Optional<Student> findByStudentId(String studentId) {
        return studentRepository.findById(studentId);
    }

    public Set<Student> findAllByStudentIdContains(String studentId) {
        return studentRepository.findAllByStudentIdContains(studentId);
    }

    public Set<Student> findAllByStudentId(String studentId) {
        return studentRepository.findAllByStudentId(studentId);
    }

    public Set<String> findDistinctCitizenships() {
        return studentRepository.findDistinctCitizenships();
    }
}

package org.dyu5thdorm.dyu5thdormdiscordbot.spring.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity()
@Table(name = "leave_temp_record")
public class LeaveTempRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @OneToOne
    @JoinColumn(name = "bed_id", referencedColumnName = "bed_id")
    Bed bed;

    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    Student student;

    @Column(name = "request_time")
    @CreationTimestamp
    LocalDateTime requestTime;

    @Column(name = "reason")
    String reason;
}

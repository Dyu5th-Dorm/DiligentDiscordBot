package org.dyu5thdorm.dyu5thdormdiscordbot.spring.repositories;

import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.living_record.LivingRecord;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.living_record.LivingRecordId;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.school_timestamp.SchoolTimestamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;


public interface LivingRecordRepo extends JpaRepository<LivingRecord, LivingRecordId> {
    Optional<LivingRecord> findByStudentStudentIdAndSchoolTimestampEquals(String student_studentId, SchoolTimestamp schoolTimestamp);
    Set<LivingRecord> findAllByBedBedIdContainsAndSchoolTimestampEquals(String bed_bedId, SchoolTimestamp schoolTimestamp);
    Optional<LivingRecord> findByBedBedIdAndAndSchoolTimestampEquals(String bed_bedId, SchoolTimestamp schoolTimestamp);
    Set<LivingRecord> findAllByStudentStudentIdAndSchoolTimestampEquals(String student_studentId, SchoolTimestamp schoolTimestamp);
    Set<LivingRecord> findAllByStudentStudentIdContainsAndSchoolTimestampEquals(String student_studentId, SchoolTimestamp schoolTimestamp);
    Set<LivingRecord> findAllByStudentNameContainsAndSchoolTimestampEquals(String student_name, SchoolTimestamp schoolTimestamp);
    Set<LivingRecord> findAllByBedBedIdStartingWith(String bed_bedId);

    @Query("""
            select distinct lr.student.citizenship
            from LivingRecord lr
            where lr.schoolTimestamp = :timestamp
              and lr.student.citizenship is not null
              and trim(lr.student.citizenship) <> ''
            """)
    Set<String> findDistinctCitizenshipsBySchoolTimestamp(@Param("timestamp") SchoolTimestamp timestamp);
}

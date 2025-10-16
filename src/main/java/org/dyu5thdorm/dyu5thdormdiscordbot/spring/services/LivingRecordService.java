package org.dyu5thdorm.dyu5thdormdiscordbot.spring.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.DiscordLink;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.living_record.LivingRecord;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.school_timestamp.SchoolTimestamp;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.repositories.LivingRecordRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LivingRecordService {
    final
    LivingRecordRepo livingRecordRepository;
    final
    DiscordLinkService discordLinkService;

    @Value("${school_year}")
    private Integer schoolYear;
    @Value("${semester}")
    private Integer semester;
    private SchoolTimestamp schoolTimestamp;

    @PostConstruct
    private void setSchoolTimestamp() {
        schoolTimestamp = new SchoolTimestamp();
        schoolTimestamp.setSchoolYear(schoolYear);
        schoolTimestamp.setSemester(semester);
    }

    public Optional<LivingRecord> findByStudentId(String studentId) {
        return livingRecordRepository.findByStudentStudentIdAndSchoolTimestampEquals(studentId, schoolTimestamp);
    }

    public Set<LivingRecord> findAllByStudentIdContains(String studentId) {
        return livingRecordRepository.findAllByStudentStudentIdContainsAndSchoolTimestampEquals(studentId, schoolTimestamp);
    }

    public Set<LivingRecord> findAllByRoomId(String roomId) {
        return livingRecordRepository.findAllByBedBedIdContainsAndSchoolTimestampEquals(roomId, schoolTimestamp);
    }

    public Optional<LivingRecord> findAllByBedId(String bedId) {
        return livingRecordRepository.findByBedBedIdAndAndSchoolTimestampEquals(bedId, schoolTimestamp);
    }

    public Set<LivingRecord> findAllByNameContains(String name) {
        return livingRecordRepository.findAllByStudentNameContainsAndSchoolTimestampEquals(name, schoolTimestamp);
    }

    public Optional<LivingRecord> findLivingRecordByDiscordId(String discordId) {
        Optional<DiscordLink> discordLink = discordLinkService.findByDiscordId(discordId);
        if (discordLink.isEmpty()) return Optional.empty();
        return findByStudentId(discordLink.get().getStudent().getStudentId());
    }

    public Set<LivingRecord> findAllByFloor(Integer floor) {
        return livingRecordRepository.findAllByBedBedIdStartingWith(
                "5" + floor.toString()
        );
    }

    public Set<String> findDistinctCitizenshipsForCurrentTerm() {
        return livingRecordRepository.findDistinctCitizenshipsBySchoolTimestamp(schoolTimestamp);
    }
}


package org.dyu5thdorm.dyu5thdormdiscordbot;

import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.attendance.AttendanceRecord;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.repositories.AttendanceRecordRepo;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.repositories.NoCallRollDateRepo;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.LeaveTempRecordService;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.LivingRecordService;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.NoCallRollDateService;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.TookCoinService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Dyu5thDormDiscordBotApplicationTests {
    @Autowired
    LeaveTempRecordService leaveTempRecordService;
    @Autowired
    NoCallRollDateService service;
    @Autowired
    LivingRecordService livingRecordService;
    @Autowired
    NoCallRollDateService noCallRollDateService;
    @Autowired
    NoCallRollDateRepo noCallRollDateRepository;
    @Autowired
    TookCoinService tookCoinService;
    @Autowired
    AttendanceRecordRepo attendanceRecordRepository;

    @Test
    void contextLoads() {
        for (AttendanceRecord attendanceRecord : attendanceRecordRepository.findAll()) {
            System.out.println(attendanceRecord);
        }
    }

}

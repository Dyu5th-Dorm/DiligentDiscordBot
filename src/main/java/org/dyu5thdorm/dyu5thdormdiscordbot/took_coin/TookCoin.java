package org.dyu5thdorm.dyu5thdormdiscordbot.took_coin;

import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.DiscordLink;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.FloorArea;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.Student;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.DiscordLinkService;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.TookCoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class TookCoin {
    public enum Type {
        WASH_MACHINE,
        DRYER,
        VENDING
    }

    public enum FailReason {
        DATE_AFTER_NOW,
        TIME_REPEAT,
        NONE,
    }

    @Autowired
    TookCoinService tookCoinService;
    @Autowired
    DiscordLinkService discordLinkService;

    FloorArea getFloorArea(Type type, String floorOrFloorArea) {
        FloorArea floorArea = new FloorArea();
        if (type == Type.VENDING) {
            floorArea.setFloor(Long.parseLong(floorOrFloorArea));
            if (floorArea.getFloor() == 1L) {
                floorArea.setAreaId("AB");
            } else {
                floorArea.setAreaId("CD");
            }

        } else {
            Long floor = Long.parseLong(floorOrFloorArea.substring(0, 1));
            String areaId = floorOrFloorArea.substring(1, 3);
            floorArea.setFloor(floor);
            floorArea.setAreaId(areaId);
        }

        return floorArea;
    }

    public FailReason record(Type type, List<String> args, Student student) {
        org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.TookCoin tookCoinModel = new org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.TookCoin();
        tookCoinModel.setStudent(student);
        FloorArea floorArea = this.getFloorArea(type, args.get(0));

        tookCoinModel.setFloor(floorArea);
        tookCoinModel.setDescription(args.get(1));
        tookCoinModel.setCoinAmount(
                Integer.valueOf(args.get(2))
        );
        tookCoinModel.setMachine(type.name());
        tookCoinModel.setTime(
                getLocalDateTime(args.get(3))
        );
        if (tookCoinModel.getTime().isAfter(LocalDateTime.now())) {
            return FailReason.DATE_AFTER_NOW;
        }

        if (tookCoinService.existsByTimeAndStudentId(
                tookCoinModel.getTime(), student.getStudentId())
        ) {
            return FailReason.TIME_REPEAT;
        }

        tookCoinService.save(tookCoinModel);

        return FailReason.NONE;
    }

    LocalDateTime getLocalDateTime(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));
        int hour = Integer.parseInt(date.substring(9, 11));
        int minute = Integer.parseInt(date.substring(11, 13));
        return LocalDateTime.of(
                year, month, day, hour, minute
        );
    }

    public Set<org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.TookCoin> getRecordByDiscordId(String discordId) {
        DiscordLink discordLink = discordLinkService.findByDiscordId(discordId);
        if (discordLink == null) {
            return null;
        }

        Student student = discordLink.getStudent();

        return tookCoinService.findByStudentId(student.getStudentId());
    }
}

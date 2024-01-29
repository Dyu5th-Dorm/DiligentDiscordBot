package org.dyu5thdorm.dyu5thdormdiscordbot.took_coin;

import lombok.AllArgsConstructor;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.DiscordLink;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.Student;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.floor_area.FloorArea;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.DiscordLinkService;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.services.TookCoinService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class TookCoinHandler {
    final TookCoinService tookCoinService;
    final DiscordLinkService discordLinkService;

    public enum MachineType { WASH_MACHINE, DRYER, VENDING }

    public enum FailReason { DATE_AFTER_NOW, TIME_REPEAT, NONE }

    FloorArea getFloorArea(MachineType type, String floorOrFloorArea) {
        FloorArea floorArea = new FloorArea();
        if (type == MachineType.VENDING) {
            floorArea.setFloor(Integer.parseInt(floorOrFloorArea));
            if (floorArea.getFloor() == 1L) {
                floorArea.setAreaId("AB");
            } else {
                floorArea.setAreaId("CD");
            }
        } else {
            Integer floor = Integer.parseInt(floorOrFloorArea.substring(0, 1));
            String areaId = floorOrFloorArea.substring(1, 3);
            floorArea.setFloor(floor);
            floorArea.setAreaId(areaId);
        }

        return floorArea;
    }

    public FailReason record(MachineType type, List<String> args, Student student) {
        org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.TookCoin tookCoinModel = new org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.TookCoin();
        tookCoinModel.setStudent(student);
        FloorArea floorArea = this.getFloorArea(type, args.get(0));

        tookCoinModel.setFloor(floorArea);
        tookCoinModel.setDescription(args.get(1));
        tookCoinModel.setCoinAmount(
                Integer.valueOf(args.get(2))
        );
        tookCoinModel.setMachine(type.name());
        tookCoinModel.setEventTime(
                getLocalDateTime(args.get(3))
        );
        if (tookCoinModel.getEventTime().isAfter(LocalDateTime.now())) {
            return FailReason.DATE_AFTER_NOW;
        }

        if (tookCoinService.existsByTimeAndStudentId(
                tookCoinModel.getEventTime(), student.getStudentId())
        ) {
            return FailReason.TIME_REPEAT;
        }

        tookCoinService.save(tookCoinModel);

        return FailReason.NONE;
    }

    public LocalDateTime getLocalDateTime(@NotNull String date) {
        int year = LocalDate.now().getYear();
        int month = Integer.parseInt(date.substring(0, 2));
        int day = Integer.parseInt(date.substring(2, 4));
        int hour = Integer.parseInt(date.substring(5, 7));
        int minute = Integer.parseInt(date.substring(7, 9));
        return LocalDateTime.of(
                year, month, day, hour, minute
        );
    }

    public Set<org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.TookCoin> getRecordUnGetByDiscordId(String discordId) {
        Optional<DiscordLink> discordLink = getDiscordLinkByDiscordId(discordId);
        if (discordLink.isEmpty()) return Set.of();
        Student student = discordLink.get().getStudent();

        return tookCoinService.findUnGetByStudentId(student.getStudentId());
    }

    Optional<DiscordLink> getDiscordLinkByDiscordId(String discordId) {
        return discordLinkService.findByDiscordId(discordId);
    }
}

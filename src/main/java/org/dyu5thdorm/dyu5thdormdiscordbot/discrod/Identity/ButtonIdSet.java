package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@Getter
@Setter(AccessLevel.NONE)
@PropertySource("classpath:discord-id-list.properties")
public class ButtonIdSet {
    @Value("${component.button.auth}")
    String auth;
    @Value("${component.button.repair}")
    String repair;
    @Value("${component.button.student-info-by-discord}")
    String searchByDiscordId;
    @Value("${component.button.student-info-by-bedId}")
    String searchByBedId;
    @Value("${component.button.student-info-by-studentId}")
    String searchByStudentId;
    @Value("${component.button.student-info-by-name}")
    String searchByStudentName;
    @Value("${component.button.shutdown}")
    String shutdown;
    @Value("${component.button.request-for-leave}")
    String reqForLeave;
    @Value("${component.button.generate-attendance-rate}")
    String generateAttendanceRate;
    @Value("${component.button.attendance-rate}")
    String attendanceRate;
    @Value("${component.button.request-for-leave-cadre}")
    String reqForLeaveCadre;
    @Value("${component.button.maintenance}")
    String maintenance;
    @Value("${component.button.generate-rules}")
    String generateRules;
    @Value("${component.button.generate-request-leave}")
    String generateReqLev;
    @Value("${component.button.generate-request-leave-cadre}")
    String generateReqLevCadre;
    @Value("${component.button.generate-request-auth}")
    String generateReqAuth;
    @Value("${component.button.generate-request-repair}")
    String generateReqRepair;
    @Value("${component.button.generate-request-took-coin}")
    String generateReqTookCoin;
    @Value("${component.button.generate-request-cadre}")
    String generateReqCadre;
    @Value("${component.button.generate-request-vote}")
    String generateVote;
    @Value("${component.button.generate-dev-channels}")
    String generateDevChannels;
    @Value("${component.button.generate-dev-roles}")
    String generateDevRoles;
    @Value("${component.button.refresh-admin-operations}")
    String refreshAdminOperations;
    @Value("${component.button.took-coin}")
    String tookCoin;
    @Value("${component.button.floor-role-correction}")
    String floorRoleCorrection;
    @Value("${component.button.nationality-role-correction}")
    String nationalityRoleCorrection;
    @Value("${component.button.took-coin-get-back}")
    String tookCoinGetBack;
    @Value("${component.button.took-coin-get-back-confirm}")
    String tookCoinGetBackConfirm;
    @Value("${component.button.took-coin-get-back-merge}")
    String tookCoinGetBackMerge;
    @Value("${component.button.took-coin-get-back-confirm-confirm}")
    String tookCoinGetBackMergeConfirm;
    @Value("${component.button.took-coin-return}")
    String tookCoinReturn;
    @Value("${component.button.ip-lookup}")
    String ipLookup;
    @Value("${component.button.attendance}")
    String attendance;
    @Value("${component.button.attendance.prev}")
    String attendancePrev;
    @Value("${component.button.attendance.next}")
    String attendanceNext;
    @Value("${component.button.attendance.all-in}")
    String attendanceAllIn;
    @Value("${component.button.attendance.all-out}")
    String attendanceAllOut;
    @Value("${component.button.attendance.out}")
    String attendanceOut;
    @Value("${component.button.attendance.complete}")
    String attendanceComplete;
    @Value("${component.button.generate-activity}")
    String generateActivity;
    @Value("${component.button.lottery}")
    String lottery;
    @Value("${component.button.activity-participate}")
    String activityParticipate;
    @Value("${component.button.activity-cancel}")
    String activityCancel;
    @Value("${component.button.activity-agree}")
    String agree;
    @Value("${component.button.activity-disagree}")
    String disagree;
    @Value("${component.button.activity-abstention}")
    String abstention;
    @Value("${component.button.took-coin-return-by-firm}")
    String returnByFirm;
}

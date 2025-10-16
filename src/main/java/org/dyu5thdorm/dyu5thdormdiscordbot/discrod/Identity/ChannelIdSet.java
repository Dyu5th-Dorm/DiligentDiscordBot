package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter(AccessLevel.NONE)
public class ChannelIdSet {
    @Value("${channel.auth}")
    String auth;
    @Value("${channel.auth-logger}")
    String authLogger;
    @Value("${channel.announcement}")
    String announcement;
    @Value("${channel.leave}")
    String leave;
    @Value("${channel.cadre-button}")
    String cadreButton;
    @Value("${channel.repair}")
    String repair;
    @Value("${channel.took-coin}")
    String tookCoin;
    @Value("${channel.admin.operation}")
    String adminOperation;
    @Value("${channel.rules}")
    String rules;
    @Value("${channel.request-leave}")
    String reqLev;
    @Value("${channel.request-leave-cadre}")
    String reqLevCadre;
    @Value("${channel.took-coin-cadre}")
    String tookCoinCadre;
    @Value("${channel.took-coin-get-back-cadre}")
    String tookCoinGetBackCadre;
    @Value("${channel.floor-one}")
    String floorOne;
    @Value("${channel.floor-two}")
    String floorTwo;
    @Value("${channel.floor-three}")
    String floorThree;
    @Value("${channel.floor-four}")
    String floorFour;
    @Value("${channel.floor-five}")
    String floorFive;
    @Value("${channel.floor-six}")
    String floorSix;
    @Value("${channel.public}")
    String publicChannel;
    @Value("${channel.lottery}")
    String lottery;
    @Value("${channel.vote}")
    String vote;
    @Value("${channel.leader}")
    String leader;
    @Value("${channel.return-by-firm}")
    String returnByFirm;
    @Value("${channel.repairment.normal}")
    String normalRepairment;
    @Value("${channel.repairment.vending}")
    String vendingRepairment;
    @Value("${channel.repairment.machine}")
    String machineRepairment;
    @Value("${channel.repairment.water}")
    String waterDispenserRepairment;

    public String findChannelId(String key) {
        return switch (key) {
            case "channel.auth" -> auth;
            case "channel.auth-logger" -> authLogger;
            case "channel.announcement" -> announcement;
            case "channel.leave" -> leave;
            case "channel.cadre-button" -> cadreButton;
            case "channel.repair" -> repair;
            case "channel.took-coin" -> tookCoin;
            case "channel.admin.operation" -> adminOperation;
            case "channel.rules" -> rules;
            case "channel.request-leave" -> reqLev;
            case "channel.request-leave-cadre" -> reqLevCadre;
            case "channel.took-coin-cadre" -> tookCoinCadre;
            case "channel.took-coin-get-back-cadre" -> tookCoinGetBackCadre;
            case "channel.floor-one" -> floorOne;
            case "channel.floor-two" -> floorTwo;
            case "channel.floor-three" -> floorThree;
            case "channel.floor-four" -> floorFour;
            case "channel.floor-five" -> floorFive;
            case "channel.floor-six" -> floorSix;
            case "channel.public" -> publicChannel;
            case "channel.lottery" -> lottery;
            case "channel.vote" -> vote;
            case "channel.leader" -> leader;
            case "channel.return-by-firm" -> returnByFirm;
            case "channel.repairment.normal" -> normalRepairment;
            case "channel.repairment.vending" -> vendingRepairment;
            case "channel.repairment.machine" -> machineRepairment;
            case "channel.repairment.water" -> waterDispenserRepairment;
            default -> throw new IllegalArgumentException("Unknown channel key: " + key);
        };
    }

    public void overrideChannelId(String key, String value) {
        switch (key) {
            case "channel.auth" -> auth = value;
            case "channel.auth-logger" -> authLogger = value;
            case "channel.announcement" -> announcement = value;
            case "channel.leave" -> leave = value;
            case "channel.cadre-button" -> cadreButton = value;
            case "channel.repair" -> repair = value;
            case "channel.took-coin" -> tookCoin = value;
            case "channel.admin.operation" -> adminOperation = value;
            case "channel.rules" -> rules = value;
            case "channel.request-leave" -> reqLev = value;
            case "channel.request-leave-cadre" -> reqLevCadre = value;
            case "channel.took-coin-cadre" -> tookCoinCadre = value;
            case "channel.took-coin-get-back-cadre" -> tookCoinGetBackCadre = value;
            case "channel.floor-one" -> floorOne = value;
            case "channel.floor-two" -> floorTwo = value;
            case "channel.floor-three" -> floorThree = value;
            case "channel.floor-four" -> floorFour = value;
            case "channel.floor-five" -> floorFive = value;
            case "channel.floor-six" -> floorSix = value;
            case "channel.public" -> publicChannel = value;
            case "channel.lottery" -> lottery = value;
            case "channel.vote" -> vote = value;
            case "channel.leader" -> leader = value;
            case "channel.return-by-firm" -> returnByFirm = value;
            case "channel.repairment.normal" -> normalRepairment = value;
            case "channel.repairment.vending" -> vendingRepairment = value;
            case "channel.repairment.machine" -> machineRepairment = value;
            case "channel.repairment.water" -> waterDispenserRepairment = value;
            default -> throw new IllegalArgumentException("Unknown channel key: " + key);
        }
    }
}

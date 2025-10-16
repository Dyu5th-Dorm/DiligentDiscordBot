package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter(AccessLevel.NONE)
public class RoleIdSet {
    @Value("${role.manager}")
    private String manager;
    @Value("${role.cadre_leader}")
    private String cadreLeader;
    @Value("${role.cadre}")
    private String cadre;
    @Value("${role.dev}")
    private String developer;
    @Value("${role.floor_one}")
    private String floorOne;
    @Value("${role.floor_two}")
    private String floorTwo;
    @Value("${role.floor_three}")
    private String floorThree;
    @Value("${role.floor_four}")
    private String floorFour;
    @Value("${role.floor_five}")
    private String floorFive;
    @Value("${role.floor_six}")
    private String floorSix;

    public String findRoleId(String key) {
        return switch (key) {
            case "role.manager" -> manager;
            case "role.cadre_leader" -> cadreLeader;
            case "role.cadre" -> cadre;
            case "role.dev" -> developer;
            case "role.floor_one" -> floorOne;
            case "role.floor_two" -> floorTwo;
            case "role.floor_three" -> floorThree;
            case "role.floor_four" -> floorFour;
            case "role.floor_five" -> floorFive;
            case "role.floor_six" -> floorSix;
            default -> throw new IllegalArgumentException("Unknown role key: " + key);
        };
    }

    public void overrideRoleId(String key, String value) {
        switch (key) {
            case "role.manager" -> manager = value;
            case "role.cadre_leader" -> cadreLeader = value;
            case "role.cadre" -> cadre = value;
            case "role.dev" -> developer = value;
            case "role.floor_one" -> floorOne = value;
            case "role.floor_two" -> floorTwo = value;
            case "role.floor_three" -> floorThree = value;
            case "role.floor_four" -> floorFour = value;
            case "role.floor_five" -> floorFive = value;
            case "role.floor_six" -> floorSix = value;
            default -> throw new IllegalArgumentException("Unknown role key: " + key);
        }
    }
}

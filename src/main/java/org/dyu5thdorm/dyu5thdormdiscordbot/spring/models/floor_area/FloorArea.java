package org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.floor_area;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "floor_area")
@IdClass(FloorAreaId.class)
@Getter
@Setter
@ToString
public class FloorArea {
    @Id
    @Column(name = "floor")
    private Integer floor;

    @Id
    @Column(name = "area_id")
    private String areaId;

    @Column(name = "start_room_id")
    private Integer startRoomId;

    @Column(name = "end_room_id")
    private Integer endRoomId;
}

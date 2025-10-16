package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.dyu5thdorm.dyu5thdormdiscordbot.discrod.Identity.ChannelIdSet;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChannelOperation {
    @Getter
    final
    ChannelIdSet channelIdSet;
    @Getter
    Map<Integer, String > floorChannelMap;

    @PostConstruct
    void init() {
        refreshFloorChannelMap();
    }

    public void deleteAllMessage(@NotNull TextChannel textChannel, int limit) {
        textChannel.getHistoryFromBeginning(limit).complete().getRetrievedHistory().forEach(
                message -> message.delete().queue()
        );
    }

    public void refreshFloorChannelMap() {
        Map<Integer, String> map = new HashMap<>();
        putIfPresent(map, 1, channelIdSet.getFloorOne());
        putIfPresent(map, 2, channelIdSet.getFloorTwo());
        putIfPresent(map, 3, channelIdSet.getFloorThree());
        putIfPresent(map, 4, channelIdSet.getFloorFour());
        putIfPresent(map, 5, channelIdSet.getFloorFive());
        putIfPresent(map, 6, channelIdSet.getFloorSix());
        floorChannelMap = Collections.unmodifiableMap(map);
    }

    private void putIfPresent(Map<Integer, String> target, int floor, String channelId) {
        if (channelId == null || channelId.isBlank()) return;
        target.put(floor, channelId);
    }
}

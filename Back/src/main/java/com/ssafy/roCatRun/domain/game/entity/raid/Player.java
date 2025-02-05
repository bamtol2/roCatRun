package com.ssafy.roCatRun.domain.game.entity.raid;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class Player {
    private String id;
    private String nickname;
    private String socketId;
    private RunningData runningData = new RunningData();
    private List<Item> items = new ArrayList<>();
    private int usedItemCount = 0;

    public Player(String id) {
        this.id = id;
        this.items = new ArrayList<>();
        this.runningData = new RunningData();
        this.usedItemCount = 0;
    }

    public Player(String id, String nickname, String socketId) {
        this.id = id;
        this.nickname = nickname;
        this.socketId = socketId;
        this.items = new ArrayList<>();
        this.runningData = new RunningData();
    }

    public void updateRunningData(RunningData newData) {
        this.runningData = newData;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void useItem() {
        this.usedItemCount++;
    }

    public void resetItemCount() {
        this.usedItemCount = 0;
    }
}
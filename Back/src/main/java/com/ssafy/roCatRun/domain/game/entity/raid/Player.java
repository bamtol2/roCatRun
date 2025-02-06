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
    private int itemCountForFever = 0;  // 피버타임을 위한 아이템 카운트

    public Player(String id) {
        this.id = id;
        this.items = new ArrayList<>();
        this.runningData = new RunningData();
        this.usedItemCount = 0;
        this.itemCountForFever=0;
    }

    public Player(String id, String nickname, String socketId) {
        this.id = id;
        this.nickname = nickname;
        this.socketId = socketId;
        this.items = new ArrayList<>();
        this.runningData = new RunningData();
        this.usedItemCount=0;
        this.itemCountForFever=0;
    }

    public void updateRunningData(RunningData newData) {
        this.runningData = newData;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void useItem() {
        this.usedItemCount++;
        // 피버타임용 카운트는 이전 피버타임에서 사용한 아이템은 제외하고 카운트
        if (itemCountForFever < GameRoom.REQUIRED_ITEMS_FOR_FEVER) {
            this.itemCountForFever++;
        }
    }

    public void resetFeverItemCount() {
        this.itemCountForFever = 0;
    }
}
package com.ssafy.raidtest.raid.domain.game;

import com.ssafy.raidtest.raid.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerGameStatus { // 실시간 데이터
    private String userId; // 유저 아이디
    private double distance; // 유저가 달린 거리
    private double itemGauge; // 유저의 아이템 게이지
    private int itemUseCount; // 유저가 사용한 아이템 수
    private List<Item> items = new ArrayList<>(); // 아이템 종류
    private double damageDealt; // 보스에게 준 데미지

    public void incrementItemUseCount() {
        this.itemUseCount++;
    }
}

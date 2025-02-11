package com.ssafy.roCatRun.domain.game.entity.raid;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Item {
    private String id;
    private String name;
    private ItemType type;
    private int damage;

    public Item(String name, ItemType type, int damage) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.damage = damage;
    }

    public enum ItemType {
        ATTACK,     // 공격형
        SUPPORT,    // 보조형
        SPECIAL     // 특수형
    }
}

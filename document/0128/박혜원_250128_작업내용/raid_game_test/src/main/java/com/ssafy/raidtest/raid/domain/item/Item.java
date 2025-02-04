package com.ssafy.raidtest.raid.domain.item;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

// domain/item/Item.java
@Getter
@Setter
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이템 아이디
    private String name; // 아이템 명
    private ItemType type; // 아이템 사용 방식
    private int damage; // 아이템의 데미지
    private String description; // 아이템 설명
}

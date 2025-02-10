package com.ssafy.roCatRun.domain.item.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter @Setter
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false)
    private String name;        // 아이템 이름

    @Column(nullable = false)
    private String imageUrl;    // 아이템 이미지 경로

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;  // 아이템 카테고리 (물감, 머리띠, 풍선)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemRarity rarity;  // 아이템 희귀도

    @Column(nullable = false)
    private Double probability; // 뽑기 확률

    @Column(nullable = false)
    private Integer sellPrice;  // 판매 가격

    // 아이템 카테고리 enum
    public enum ItemCategory {
        PAINT, HEADBAND, BALLOON
    }

    // 아이템 희귀도 enum
    public enum ItemRarity {
        NORMAL(100),    // 판매가격 기본 배수: 1배
        RARE(200),      // 판매가격 기본 배수: 2배
        EPIC(400),      // 판매가격 기본 배수: 4배
        UNIQUE(800),    // 판매가격 기본 배수: 8배
        LEGENDARY(1600);// 판매가격 기본 배수: 16배

        private final int basePrice;

        ItemRarity(int basePrice) {
            this.basePrice = basePrice;
        }

        public int getBasePrice() {
            return basePrice;
        }
    }
}
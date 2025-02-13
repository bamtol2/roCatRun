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
    private String name;            // 아이템 이름

    @Column(nullable = false)
    private String imagePath;       // 아이템 이미지 경로

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;  // 아이템 카테고리

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemRarity rarity;      // 아이템 희귀도

    @Column(nullable = false)
    private Double probability;     // 뽑기 확률

    @Column(nullable = false)
    private Integer price;          // 판매 가격

    // 아이템 카테고리 정의
    public enum Category {
        AURA("effect"),    // 오라
        BALLOON("balloon"), // 풍선
        HEADBAND("headband"), // 머리띠
        PAINT("paint");    // 물감

        private final String value;

        Category(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    // 아이템 희귀도 정의
    public enum ItemRarity {
        NORMAL(0.45),    // 일반냥
        RARE(0.30),      // 레어냥
        EPIC(0.20),      // 에픽냥
        UNIQUE(0.04),    // 유니크냥
        LEGENDARY(0.01); // 레전드리냥

        private final double probability;

        ItemRarity(double probability) {
            this.probability = probability;
        }

        public double getProbability() {
            return probability;
        }
    }
}

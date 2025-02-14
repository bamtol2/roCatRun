package com.ssafy.roCatRun.domain.item.dto.response;

import com.ssafy.roCatRun.domain.item.entity.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemResponse {
    private Long id;                // 아이템 ID
    private String name;            // 아이템 영문 이름
    private String koreanName;      // 아이템 한글 이름
    private String description;     // 아이템 설명
    private Boolean isGif;          // GIF 이미지 여부
    private Item.Category category; // 아이템 카테고리
    private Item.ItemRarity rarity; // 아이템 희귀도
    private Integer price;          // 판매 가격

    public static ItemResponse from(Item item) {
        ItemResponse response = new ItemResponse();
        response.id = item.getId();
        response.name = item.getName();
        response.koreanName = item.getKoreanName();
        response.description = item.getDescription();
        response.isGif = item.getIsGif();
        response.category = item.getCategory();
        response.rarity = item.getRarity();
        response.price = item.getPrice();
        return response;
    }
}
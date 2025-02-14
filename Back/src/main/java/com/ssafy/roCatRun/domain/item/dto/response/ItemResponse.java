package com.ssafy.roCatRun.domain.item.dto.response;

import com.ssafy.roCatRun.domain.item.entity.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemResponse {
    private Long id;                // 아이템 ID
    private String name;            // 아이템 이름
    private String description;     // 아이템 설명
    private String listImage;       // 아이템 목록 이미지
    private String equipImage;      // 착용 시 이미지
    private Boolean listImageIsGif;     // 목록 이미지 GIF 여부
    private Boolean equipImageIsGif;     // 착용 이미지 GIF 여부
    private Item.Category category; // 아이템 카테고리
    private Item.ItemRarity rarity; // 아이템 희귀도
    private Integer price;          // 판매 가격

    public static ItemResponse from(Item item) {
        ItemResponse response = new ItemResponse();
        response.id = item.getId();
        response.name = item.getName();
        response.description = item.getDescription();
        response.listImage = item.getListImage();
        response.equipImage = item.getEquipImage();
        response.listImageIsGif = item.getListImageIsGif();
        response.equipImageIsGif = item.getEquipImageIsGif();
        response.category = item.getCategory();
        response.rarity = item.getRarity();
        response.price = item.getPrice();
        return response;
    }
}
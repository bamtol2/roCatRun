package com.ssafy.roCatRun.domain.item.dto.response;

import com.ssafy.roCatRun.domain.item.entity.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemResponse {
    private Long id;
    private String name;
    private String imagePath;
    private Item.Category category;
    private Item.ItemRarity rarity;
    private Integer price;

    public static ItemResponse from(Item item) {
        ItemResponse response = new ItemResponse();
        response.id = item.getId();
        response.name = item.getName();
        response.imagePath = item.getImagePath();
        response.category = item.getCategory();
        response.rarity = item.getRarity();
        response.price = item.getPrice();
        return response;
    }
}


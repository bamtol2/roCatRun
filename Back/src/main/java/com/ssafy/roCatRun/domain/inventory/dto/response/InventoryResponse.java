package com.ssafy.roCatRun.domain.inventory.dto.response;

import com.ssafy.roCatRun.domain.inventory.entity.Inventory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InventoryResponse {
    private Long inventoryId;
    private String itemName;
    private String imagePath;
    private String category;
    private String rarity;
    private int price;
    private boolean isEquipped;

    public static InventoryResponse from(Inventory inventory) {
        InventoryResponse response = new InventoryResponse();
        response.inventoryId = inventory.getId();
        response.itemName = inventory.getItem().getName();
        response.imagePath = inventory.getItem().getImagePath();
        response.category = inventory.getItem().getCategory().name();
        response.rarity = inventory.getItem().getRarity().name();
        response.price = inventory.getItem().getPrice();
        response.isEquipped = inventory.getIsEquipped();
        return response;
    }
}

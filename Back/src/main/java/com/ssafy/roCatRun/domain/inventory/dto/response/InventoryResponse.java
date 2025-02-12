package com.ssafy.roCatRun.domain.inventory.dto.response;

import com.ssafy.roCatRun.domain.inventory.entity.Inventory;
import com.ssafy.roCatRun.domain.item.entity.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인벤토리 아이템 정보를 반환하기 위한 DTO
 */
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

        Item item = inventory.getItem();
        // item이 존재할 때만 아이템 정보 설정
        if (item != null) {
            response.itemName = item.getName();
            response.imagePath = item.getImagePath();
            response.category = item.getCategory().name();
            response.rarity = item.getRarity().name();
            response.price = item.getPrice();
        } else {
            // 빈 인벤토리인 경우 기본값 설정
            response.itemName = "빈 슬롯";
            response.imagePath = "default.png";
            response.category = "NONE";
            response.rarity = "NONE";
            response.price = 0;
        }

        response.isEquipped = inventory.getIsEquipped();
        return response;
    }
}
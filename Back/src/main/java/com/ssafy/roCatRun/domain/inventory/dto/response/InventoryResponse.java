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
    private Long inventoryId;       // 인벤토리 ID
    private String itemName;        // 아이템 이름
    private String description;     // 아이템 설명
    private String listImage;       // 아이템 목록 이미지
    private String equipImage;      // 착용 시 이미지
    private Boolean isGif;          // GIF 여부
    private String category;        // 아이템 카테고리
    private String rarity;          // 아이템 희귀도
    private int price;             // 판매 가격
    private boolean isEquipped;     // 착용 여부

    public static InventoryResponse from(Inventory inventory) {
        InventoryResponse response = new InventoryResponse();
        response.inventoryId = inventory.getId();

        Item item = inventory.getItem();
        // item이 존재할 때만 아이템 정보 설정
        if (item != null) {
            response.itemName = item.getName();
            response.description = item.getDescription();
            response.listImage = item.getListImage();
            response.equipImage = item.getEquipImage();
            response.isGif = item.getIsGif();
            response.category = item.getCategory().name();
            response.rarity = item.getRarity().name();
            response.price = item.getPrice();
        } else {
            // 빈 인벤토리인 경우 기본값 설정
            response.itemName = "빈 슬롯";
            response.description = "비어있는 인벤토리 슬롯입니다.";
            response.listImage = "default.png";
            response.equipImage = "default.png";
            response.isGif = false;
            response.category = "NONE";
            response.rarity = "NONE";
            response.price = 0;
        }

        response.isEquipped = inventory.getIsEquipped();
        return response;
    }
}
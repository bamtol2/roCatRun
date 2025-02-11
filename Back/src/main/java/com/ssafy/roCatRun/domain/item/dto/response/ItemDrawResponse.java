package com.ssafy.roCatRun.domain.item.dto.response;

import com.ssafy.roCatRun.domain.item.entity.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ItemDrawResponse {
    private List<DrawnItem> drawnItems;  // 뽑은 아이템 목록
    private int remainingCoins;          // 남은 코인

    public ItemDrawResponse(List<DrawnItem> drawnItems, int remainingCoins) {
        this.drawnItems = drawnItems;
        this.remainingCoins = remainingCoins;
    }

    @Getter
    @NoArgsConstructor
    public static class DrawnItem {
        private Long itemId;
        private String name;
        private String imagePath;
        private String rarity;

        public static DrawnItem from(Item item) {
            DrawnItem drawnItem = new DrawnItem();
            drawnItem.itemId = item.getId();
            drawnItem.name = item.getName();
            drawnItem.imagePath = item.getImagePath();
            drawnItem.rarity = item.getRarity().name();
            return drawnItem;
        }
    }
}
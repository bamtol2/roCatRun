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
        private Long itemId;        // 아이템 ID
        private String name;        // 아이템 영문 이름
        private String koreanName;  // 아이템 한글 이름
        private String description; // 아이템 설명
        private String rarity;      // 아이템 희귀도

        public static DrawnItem from(Item item) {
            DrawnItem drawnItem = new DrawnItem();
            drawnItem.itemId = item.getId();
            drawnItem.name = item.getName();
            drawnItem.koreanName = item.getKoreanName();
            drawnItem.description = item.getDescription();
            drawnItem.rarity = item.getRarity().name();
            return drawnItem;
        }
    }
}
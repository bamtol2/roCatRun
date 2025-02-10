package com.ssafy.roCatRun.domain.item.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ItemDrawResponse {
    private List<DrawnItem> drawnItems;  // 뽑은 아이템 목록
    private int remainingCoins;          // 남은 코인

    @Getter
    @NoArgsConstructor
    public static class DrawnItem {
        private Long itemId;
        private String name;
        private String imagePath;
        private String rarity;
    }
}
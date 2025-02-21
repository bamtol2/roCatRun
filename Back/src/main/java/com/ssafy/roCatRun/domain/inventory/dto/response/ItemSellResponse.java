package com.ssafy.roCatRun.domain.inventory.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemSellResponse {
    private Long soldInventoryId;
    private int receivedCoins;
    private int currentCoins;

    public ItemSellResponse(Long soldInventoryId, int receivedCoins, int currentCoins) {
        this.soldInventoryId = soldInventoryId;
        this.receivedCoins = receivedCoins;
        this.currentCoins = currentCoins;
    }
}
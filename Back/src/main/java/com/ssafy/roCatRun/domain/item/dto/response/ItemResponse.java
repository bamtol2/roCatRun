package com.ssafy.roCatRun.domain.item.dto.response;

import com.ssafy.roCatRun.domain.item.entity.UserItem;
import com.ssafy.roCatRun.domain.item.entity.Item;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ItemResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private Item.ItemCategory category;
    private boolean isEquipped;

    public static ItemResponse from(UserItem userItem) {
        return ItemResponse.builder()
                .id(userItem.getItem().getId())
                .name(userItem.getItem().getName())
                .imageUrl(userItem.getItem().getImageUrl())
                .category(userItem.getItem().getCategory())
                .isEquipped(userItem.isEquipped())
                .build();
    }
}

package com.ssafy.raidtest.raid.dto.request;

import com.ssafy.raidtest.raid.domain.item.ItemType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemUseRequest {
    private Long itemId;
    private ItemType itemType;
    private LocalDateTime timestamp;
}

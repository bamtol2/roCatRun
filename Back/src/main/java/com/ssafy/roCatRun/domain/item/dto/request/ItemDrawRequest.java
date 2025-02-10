package com.ssafy.roCatRun.domain.item.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemDrawRequest {
    private int drawCount;         // 뽑기 횟수 (1 또는 10)
}
package com.ssafy.roCatRun.domain.item.controller;

import com.ssafy.roCatRun.domain.item.dto.request.ItemDrawRequest;
import com.ssafy.roCatRun.domain.item.dto.response.ItemDrawResponse;
import com.ssafy.roCatRun.domain.item.service.ItemService;
import com.ssafy.roCatRun.global.common.ApiResponse;
import com.ssafy.roCatRun.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 아이템 뽑기 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/domain/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    /**
     * 아이템 뽑기 API
     * 코인을 사용하여 랜덤으로 아이템을 획득합니다.
     */
    @PostMapping("/draw")
    public ApiResponse<ItemDrawResponse> drawItem(@RequestBody ItemDrawRequest request) {
        try {
            ItemDrawResponse response = itemService.drawItem(request.getDrawCount());
            return ApiResponse.success("아이템 뽑기 성공", response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
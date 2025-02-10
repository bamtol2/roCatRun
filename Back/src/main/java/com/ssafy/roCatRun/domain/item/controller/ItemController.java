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
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    /**
     * 아이템 뽑기 API
     * 코인을 사용하여 랜덤으로 아이템을 획득합니다.
     */
    @PostMapping("/draw")
    public ApiResponse<ItemDrawResponse> drawItem(
            @AuthenticationPrincipal Member member,
            @RequestBody ItemDrawRequest request) {

        ItemDrawResponse response = itemService.drawItem(member.getId(), request.getDrawCount());

        if (response == null) {
            if (request.getDrawCount() != 1 && request.getDrawCount() != 10) {
                return ApiResponse.error("뽑기 횟수는 1회 또는 10회만 가능합니다.");
            }
            return ApiResponse.error("아이템 뽑기에 실패했습니다. 코인이 부족하거나 캐릭터 정보가 없습니다.");
        }

        return ApiResponse.success("아이템 뽑기 성공", response);
    }
}
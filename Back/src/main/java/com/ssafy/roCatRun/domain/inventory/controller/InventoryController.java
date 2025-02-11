package com.ssafy.roCatRun.domain.inventory.controller;

import com.ssafy.roCatRun.domain.inventory.dto.response.InventoryResponse;
import com.ssafy.roCatRun.domain.inventory.dto.response.ItemSellResponse;
import com.ssafy.roCatRun.domain.inventory.service.InventoryService;
import com.ssafy.roCatRun.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 인벤토리 관련 API를 처리하는 컨트롤러
 * 로그인한 회원의 액세스 토큰을 기반으로 처리
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    /**
     * 보유 아이템 조회 (전체 또는 카테고리별)
     * @param category 카테고리 (null일 경우 전체 조회)
     */
    @GetMapping("/items")
    public ApiResponse<List<InventoryResponse>> getInventoryItems(
            @RequestParam(required = false) String category) {
        List<InventoryResponse> items = (category == null)
                ? inventoryService.getInventoryItems()
                : inventoryService.getInventoryItemsByCategory(category);

        String message = category == null ? "인벤토리 전체 조회 성공" : "카테고리별 아이템 조회 성공";
        return ApiResponse.success(message, items);
    }

    /**
     * 아이템 착용/해제
     */
    @PatchMapping("/items/{inventoryId}/toggle")
    public ApiResponse<InventoryResponse> toggleItem(@PathVariable Long inventoryId) {
        return ApiResponse.success("아이템 착용 상태 변경 성공",
                inventoryService.toggleItem(inventoryId));
    }

    /**
     * 아이템 판매
     */
    @PostMapping("/items/{inventoryId}/sell")
    public ApiResponse<ItemSellResponse> sellItem(@PathVariable Long inventoryId) {
        return ApiResponse.success("아이템 판매 성공",
                inventoryService.sellItem(inventoryId));
    }
}
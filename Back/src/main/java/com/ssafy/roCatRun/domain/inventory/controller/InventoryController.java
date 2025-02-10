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
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    /**
     * 캐릭터의 전체 보유 아이템 조회
     */
    @GetMapping("/{characterId}/items")
    public ApiResponse<List<InventoryResponse>> getInventoryItems(
            @PathVariable Long characterId) {
        return ApiResponse.success("인벤토리 조회 성공",
                inventoryService.getInventoryItems(characterId));
    }

    /**
     * 캐릭터의 카테고리별 보유 아이템 조회
     */
    @GetMapping("/{characterId}/items/category/{category}")
    public ApiResponse<List<InventoryResponse>> getInventoryItemsByCategory(
            @PathVariable Long characterId,
            @PathVariable String category) {
        return ApiResponse.success("카테고리별 아이템 조회 성공",
                inventoryService.getInventoryItemsByCategory(characterId, category));
    }

    /**
     * 아이템 착용/해제
     */
    @PatchMapping("/{characterId}/items/{inventoryId}/toggle")
    public ApiResponse<InventoryResponse> toggleItem(
            @PathVariable Long characterId,
            @PathVariable Long inventoryId) {
        return ApiResponse.success("아이템 착용 상태 변경 성공",
                inventoryService.toggleItem(characterId, inventoryId));
    }

    /**
     * 아이템 판매
     */
    @PostMapping("/{characterId}/items/{inventoryId}/sell")
    public ApiResponse<ItemSellResponse> sellItem(
            @PathVariable Long characterId,
            @PathVariable Long inventoryId) {
        return ApiResponse.success("아이템 판매 성공",
                inventoryService.sellItem(characterId, inventoryId));
    }
}
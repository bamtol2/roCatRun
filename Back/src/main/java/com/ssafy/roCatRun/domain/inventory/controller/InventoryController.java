package com.ssafy.roCatRun.domain.inventory.controller;

import com.ssafy.roCatRun.domain.inventory.dto.request.InventoryEquipRequest;
import com.ssafy.roCatRun.domain.inventory.dto.request.InventorySellRequest;
import com.ssafy.roCatRun.domain.inventory.dto.response.InventoryResponse;
import com.ssafy.roCatRun.domain.inventory.dto.response.ItemSellResponse;
import com.ssafy.roCatRun.domain.inventory.service.InventoryService;
import com.ssafy.roCatRun.domain.item.entity.Item;
import com.ssafy.roCatRun.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 인벤토리 관련 API를 처리하는 컨트롤러
 * 로그인한 회원의 액세스 토큰을 기반으로 처리
 */
@RestController
@RequestMapping("/domain/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    /**
     * 보유 아이템 조회 API
     * - category 파라미터가 없으면 전체 아이템 조회
     * - category 파라미터가 있으면 해당 카테고리의 아이템만 조회
     *
     * @param category 조회할 카테고리 (effect/balloon/headband/paint)
     * @return 인벤토리 아이템 목록
     */
    @GetMapping("/items")
    public ApiResponse<List<InventoryResponse>> getInventoryItems(
            @RequestParam(required = false) String category) {
        List<InventoryResponse> items;
        String message;

        if (category == null) {
            items = inventoryService.getInventoryItems();
            message = "인벤토리 전체 조회 성공";
        } else {
            // 카테고리 값 검증
            validateCategory(category);
            items = inventoryService.getInventoryItemsByCategory(category);
            message = category + " 카테고리 아이템 조회 성공";
        }

        return ApiResponse.success(message, items);
    }

    /**
     * 중복 제거된 보유 아이템 조회 API
     * - category 파라미터가 없으면 전체 아이템 조회
     * - category 파라미터가 있으면 해당 카테고리의 아이템만 조회
     * - 같은 아이템은 하나만 표시
     *
     * @param category 조회할 카테고리 (effect/balloon/headband/paint)
     * @return 중복 제거된 인벤토리 아이템 목록
     */
    @GetMapping("/items/distinct")
    public ApiResponse<List<InventoryResponse>> getDistinctInventoryItems(
            @RequestParam(required = false) String category) {
        List<InventoryResponse> items;
        String message;

        if (category == null) {
            items = inventoryService.getDistinctInventoryItems();
            message = "중복 제거된 인벤토리 전체 조회 성공";
        } else {
            validateCategory(category);
            items = inventoryService.getDistinctInventoryItemsByCategory(category);
            message = category + " 카테고리 중복 제거된 아이템 조회 성공";
        }

        return ApiResponse.success(message, items);
    }

    /**
     * 카테고리 값 검증
     * 잘못된 카테고리가 입력되면 예외 발생
     */
    private void validateCategory(String category) {
        try {
            Item.Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 카테고리입니다. (effect/balloon/headband/paint)");
        }
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
     * 아이템 일괄 판매
     * - 요청된 inventoryIds 리스트의 아이템들을 한 번에 판매
     * - 판매 금액을 캐릭터의 코인에 추가
     */
    @PostMapping("/items/sell-multiple")
    public ApiResponse<ItemSellResponse> sellMultipleItems(@RequestBody InventorySellRequest request) {
        return ApiResponse.success("아이템 일괄 판매 성공",
                inventoryService.sellMultipleItems(request.getInventoryIds(), request.getTotalPrice()));
    }

    /**
     * 아이템 일괄 착용/해제
     * - 요청된 inventoryIds 리스트의 아이템들만 착용 상태로 변경
     * - 나머지 아이템들은 모두 해제 상태로 변경
     */
    @PutMapping("/items/equip")
    public ApiResponse<List<InventoryResponse>> equipItems(@RequestBody InventoryEquipRequest request) {
        return ApiResponse.success("아이템 착용 상태 변경 성공",
                inventoryService.equipItems(request.getInventoryIds()));
    }
}
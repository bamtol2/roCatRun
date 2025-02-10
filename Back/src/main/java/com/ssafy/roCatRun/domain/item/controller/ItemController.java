package com.ssafy.roCatRun.domain.item.controller;

import com.ssafy.roCatRun.domain.item.dto.response.ItemResponse;
import com.ssafy.roCatRun.domain.item.entity.Item;
import com.ssafy.roCatRun.domain.item.entity.UserItem;  // UserItem import 추가
import com.ssafy.roCatRun.domain.item.repository.UserItemRepository;
import com.ssafy.roCatRun.domain.item.service.ItemService;
import com.ssafy.roCatRun.domain.member.entity.Member;
import com.ssafy.roCatRun.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;  // ResponseEntity import 추가
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 아이템 관련 API를 처리하는 컨트롤러
 * 아이템 조회, 착용/해제, 판매, 뽑기 등의 엔드포인트를 제공
 */
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    // 아이템 관련 비즈니스 로직을 처리하는 서비스
    private final ItemService itemService;

    // 유저 아이템 데이터 접근을 위한 레포지토리
    private final UserItemRepository userItemRepository;

    /**
     * 사용자의 전체 아이템 목록을 조회
     * @param member 현재 로그인한 회원 정보
     * @return 보유 중인 전체 아이템 목록
     */
    @GetMapping
    public ResponseEntity<List<ItemResponse>> getUserItems(@AuthenticationPrincipal Member member) {
        List<ItemResponse> items = itemService.getUserItems(member.getCharacter()).stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }

    /**
     * 특정 카테고리의 아이템 목록을 조회
     * @param member 현재 로그인한 회원 정보
     * @param category 조회할 아이템 카테고리 (PAINT, HEADBAND, BALLOON)
     * @return 해당 카테고리의 아이템 목록
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ItemResponse>> getUserItemsByCategory(
            @AuthenticationPrincipal Member member,
            @PathVariable Item.ItemCategory category) {
        List<ItemResponse> items = itemService.getUserItemsByCategory(member.getCharacter(), category).stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }

    /**
     * 아이템 착용/해제 상태를 토글
     * @param member 현재 로그인한 회원 정보
     * @param itemId 착용/해제할 아이템 ID
     * @return 처리 결과
     */
    @PostMapping("/{itemId}/toggle")
    public ResponseEntity<Void> toggleEquipItem(
            @AuthenticationPrincipal Member member,
            @PathVariable Long itemId) {
        itemService.toggleEquipItem(member.getCharacter(), itemId);
        return ResponseEntity.ok().build();
    }

    /**
     * 아이템 판매
     * @param member 현재 로그인한 회원 정보
     * @param itemId 판매할 아이템 ID
     * @return 판매 금액
     */
    @PostMapping("/{itemId}/sell")
    public ResponseEntity<Integer> sellItem(
            @AuthenticationPrincipal Member member,
            @PathVariable Long itemId) {
        int sellPrice = itemService.sellItem(member.getCharacter(), itemId);
        return ResponseEntity.ok(sellPrice);
    }

    /**
     * 아이템 뽑기 (가챠)
     * @param member 현재 로그인한 회원 정보
     * @return 뽑은 아이템 정보
     */
    @PostMapping("/draw")
    public ResponseEntity<ItemResponse> drawItem(@AuthenticationPrincipal Member member) {
        // 아이템 뽑기 실행
        Item drawnItem = itemService.drawItem(member.getCharacter());

        // 뽑은 아이템의 UserItem 정보 조회
        UserItem userItem = userItemRepository.findByCharacterAndItem_Id(
                member.getCharacter(),
                drawnItem.getId()
        ).orElseThrow(() -> new RuntimeException("Failed to create user item"));

        // ItemResponse로 변환하여 반환
        return ResponseEntity.ok(ItemResponse.from(userItem));
    }
}
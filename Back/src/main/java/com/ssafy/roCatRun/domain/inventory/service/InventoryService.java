package com.ssafy.roCatRun.domain.inventory.service;

import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import com.ssafy.roCatRun.domain.inventory.dto.response.InventoryResponse;
import com.ssafy.roCatRun.domain.inventory.dto.response.ItemSellResponse;
import com.ssafy.roCatRun.domain.inventory.entity.Inventory;
import com.ssafy.roCatRun.domain.inventory.repository.InventoryRepository;
import com.ssafy.roCatRun.domain.item.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 인벤토리 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 로그인한 회원의 캐릭터 아이템 관리와 관련된 모든 기능을 담당
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    /**
     * 현재 로그인한 회원의 캐릭터가 보유한 전체 인벤토리 아이템을 조회합니다.
     * SecurityContext에서 현재 인증된 회원의 ID를 가져와 조회합니다.
     *
     * @return 회원이 보유한 모든 아이템 목록
     */
    public List<InventoryResponse> getInventoryItems() {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return inventoryRepository.findByGameCharacter_Member_Id(memberId)
                .stream()
                .map(InventoryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 현재 로그인한 회원의 캐릭터가 보유한 아이템을 카테고리별로 조회합니다.
     * SecurityContext에서 현재 인증된 회원의 ID를 가져와 조회합니다.
     *
     * @param category 조회할 아이템 카테고리 (effect/balloon/headband/paint)
     * @return 해당 카테고리의 아이템 목록
     * @throws IllegalArgumentException 잘못된 카테고리가 입력된 경우
     */
    public List<InventoryResponse> getInventoryItemsByCategory(String category) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return inventoryRepository.findByGameCharacter_Member_IdAndItem_Category(
                        memberId,
                        Item.Category.valueOf(category.toUpperCase())
                )
                .stream()
                .map(InventoryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 아이템의 착용 상태를 변경합니다.
     * 아이템을 착용할 경우, 같은 카테고리의 기존 착용 아이템은 자동으로 해제됩니다.
     *
     * @param inventoryId 착용/해제할 인벤토리 아이템 ID
     * @return 상태가 변경된 아이템 정보
     * @throws IllegalArgumentException 인벤토리 아이템이 존재하지 않거나 접근 권한이 없는 경우
     */
    @Transactional
    public InventoryResponse toggleItem(Long inventoryId) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Inventory inventory = validateInventoryAccess(inventoryId, memberId);

        if (!inventory.getIsEquipped()) {
            // 아이템 착용 시 같은 카테고리의 기존 착용 아이템 해제
            inventoryRepository
                    .findByGameCharacter_Member_IdAndItem_CategoryAndIsEquippedTrue(
                            memberId,
                            inventory.getItem().getCategory()
                    )
                    .ifPresent(equippedItem -> equippedItem.setIsEquipped(false));
        }

        inventory.toggleEquipped();
        return InventoryResponse.from(inventory);
    }

    /**
     * 인벤토리의 아이템을 판매합니다.
     * 착용 중인 아이템은 판매할 수 없으며, 판매 시 아이템 가격의 50%를 코인으로 받습니다.
     *
     * @param inventoryId 판매할 인벤토리 아이템 ID
     * @return 판매 결과 정보 (판매된 아이템 ID, 받은 코인, 현재 보유 코인)
     * @throws IllegalArgumentException 인벤토리 아이템이 존재하지 않거나 접근 권한이 없는 경우
     * @throws IllegalStateException 착용 중인 아이템을 판매하려고 할 경우
     */
    @Transactional
    public ItemSellResponse sellItem(Long inventoryId) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Inventory inventory = validateInventoryAccess(inventoryId, memberId);
        GameCharacter character = inventory.getGameCharacter();

        if (inventory.getIsEquipped()) {
            throw new IllegalStateException("착용 중인 아이템은 판매할 수 없습니다.");
        }

        int sellPrice = inventory.getItem().getPrice() / 2;  // 판매가는 구매가의 50%
        character.addCoin(sellPrice);

        inventoryRepository.delete(inventory);

        return new ItemSellResponse(inventoryId, sellPrice, character.getCoin());
    }

    /**
     * 인벤토리 아이템 접근을 위한 검증
     * 현재 로그인한 회원의 소유 아이템인지 확인합니다.
     *
     * @param inventoryId 검증할 인벤토리 아이템 ID
     * @param memberId 현재 로그인한 회원 ID
     * @return 검증된 인벤토리 아이템
     * @throws IllegalArgumentException 존재하지 않거나 접근 권한이 없는 아이템인 경우
     */
    private Inventory validateInventoryAccess(Long inventoryId, Long memberId) {
        return inventoryRepository.findByIdAndGameCharacter_Member_Id(inventoryId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 접근 권한이 없는 인벤토리 아이템입니다."));
    }
}
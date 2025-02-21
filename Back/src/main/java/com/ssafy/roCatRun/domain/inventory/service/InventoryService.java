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

import java.util.Comparator;
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
     * 같은 아이템끼리 연속되도록 정렬하여 반환합니다.
     *
     * @return 회원이 보유한 모든 아이템 목록 (같은 아이템끼리 연속 정렬됨)
     */
    public List<InventoryResponse> getInventoryItems() {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return inventoryRepository.findByGameCharacter_Member_Id(memberId)
                .stream()
                .filter(inventory -> inventory.getItem() != null)  // 아이템이 있는 인벤토리만 필터링
                .map(InventoryResponse::from)
                .sorted(Comparator.comparing(response -> response.getName()))  // 아이템 이름으로 정렬
                .collect(Collectors.toList());
    }

    /**
     * 현재 로그인한 회원의 캐릭터가 보유한 아이템을 카테고리별로 조회합니다.
     * SecurityContext에서 현재 인증된 회원의 ID를 가져와 조회합니다.
     * 같은 아이템끼리 연속되도록 정렬하여 반환합니다.
     *
     * @param category 조회할 아이템 카테고리 (effect/balloon/headband/paint)
     * @return 해당 카테고리의 아이템 목록 (같은 아이템끼리 연속 정렬됨)
     * @throws IllegalArgumentException 잘못된 카테고리가 입력된 경우
     */
    public List<InventoryResponse> getInventoryItemsByCategory(String category) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return inventoryRepository.findByGameCharacter_Member_IdAndItem_Category(
                        memberId,
                        Item.Category.valueOf(category.toUpperCase())
                )
                .stream()
                .filter(inventory -> inventory.getItem() != null)  // 아이템이 있는 인벤토리만 필터링
                .map(InventoryResponse::from)
                .sorted(Comparator.comparing(response -> response.getName()))  // 아이템 이름으로 정렬
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
     * 여러 인벤토리 아이템을 한 번에 판매합니다.
     * 착용 중인 아이템이 포함되어 있으면 판매할 수 없습니다.
     *
     * @param inventoryIds 판매할 인벤토리 아이템 ID 리스트
     * @param totalPrice 판매 총액
     * @return 판매 결과 정보 (마지막으로 판매된 아이템 ID, 받은 코인, 현재 보유 코인)
     * @throws IllegalArgumentException 인벤토리 아이템이 존재하지 않거나 접근 권한이 없는 경우
     * @throws IllegalStateException 착용 중인 아이템이 포함된 경우
     */
    @Transactional
    public ItemSellResponse sellMultipleItems(List<Long> inventoryIds, int totalPrice) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        // 판매할 모든 인벤토리 아이템 조회
        List<Inventory> inventories = inventoryIds.stream()
                .map(id -> validateInventoryAccess(id, memberId))
                .collect(Collectors.toList());

        // 착용 중인 아이템이 있는지 확인
        if (inventories.stream().anyMatch(Inventory::getIsEquipped)) {
            throw new IllegalStateException("착용 중인 아이템은 판매할 수 없습니다.");
        }

        // 캐릭터의 코인 증가
        GameCharacter character = inventories.get(0).getGameCharacter();
        character.addCoin(totalPrice);

        // 인벤토리에서 아이템 삭제
        inventoryRepository.deleteAllById(inventoryIds);

        // 마지막 아이템 ID, 받은 코인, 현재 보유 코인 반환
        return new ItemSellResponse(
                inventoryIds.get(inventoryIds.size() - 1),
                totalPrice,
                character.getCoin()
        );
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

    /**
     * 아이템 착용 상태를 일괄 변경합니다.
     * 요청된 아이템들만 착용 상태로 변경하고 나머지는 모두 해제합니다.
     *
     * @param inventoryIds 착용할 인벤토리 아이템 ID 리스트
     * @return 변경된 전체 인벤토리 아이템 목록 (같은 아이템끼리 연속 정렬됨)
     */
    @Transactional
    public List<InventoryResponse> equipItems(List<Long> inventoryIds) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        // 회원의 모든 인벤토리 아이템을 조회
        List<Inventory> allInventories = inventoryRepository.findByGameCharacter_Member_Id(memberId);

        // 모든 아이템을 우선 착용 해제 상태로 변경
        allInventories.forEach(inventory -> inventory.setIsEquipped(false));

        // 요청된 ID에 해당하는 아이템만 착용 상태로 변경
        allInventories.stream()
                .filter(inventory -> inventoryIds.contains(inventory.getId()))
                .forEach(inventory -> inventory.setIsEquipped(true));

        return allInventories.stream()
                .map(InventoryResponse::from)
                .sorted(Comparator.comparing(response -> response.getName()))  // 아이템 이름으로 정렬
                .collect(Collectors.toList());
    }
    /**
     * 현재 로그인한 회원의 캐릭터가 보유한 전체 인벤토리 아이템을 중복 제거하여 조회합니다.
     * 같은 아이템은 하나만 표시됩니다.
     * SecurityContext에서 현재 인증된 회원의 ID를 가져와 조회합니다.
     *
     * @return 중복 제거된 아이템 목록
     */
    public List<InventoryResponse> getDistinctInventoryItems() {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return inventoryRepository.findByGameCharacter_Member_Id(memberId)
                .stream()
                .filter(inventory -> inventory.getItem() != null)
                .map(InventoryResponse::from)
                .collect(Collectors.groupingBy(
                        InventoryResponse::getItemId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.get(0)
                        )
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(InventoryResponse::getName))
                .collect(Collectors.toList());
    }

    /**
     * 현재 로그인한 회원의 캐릭터가 보유한 아이템을 카테고리별로 중복 제거하여 조회합니다.
     * 같은 아이템은 하나만 표시됩니다.
     * SecurityContext에서 현재 인증된 회원의 ID를 가져와 조회합니다.
     *
     * @param category 조회할 아이템 카테고리 (effect/balloon/headband/paint)
     * @return 중복 제거된 해당 카테고리의 아이템 목록
     */
    public List<InventoryResponse> getDistinctInventoryItemsByCategory(String category) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        return inventoryRepository.findByGameCharacter_Member_IdAndItem_Category(
                        memberId,
                        Item.Category.valueOf(category.toUpperCase())
                )
                .stream()
                .filter(inventory -> inventory.getItem() != null)
                .map(InventoryResponse::from)
                .collect(Collectors.groupingBy(
                        InventoryResponse::getItemId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.get(0)
                        )
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(InventoryResponse::getName))
                .collect(Collectors.toList());
    }
}
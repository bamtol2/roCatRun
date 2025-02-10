package com.ssafy.roCatRun.domain.inventory.service;

import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import com.ssafy.roCatRun.domain.inventory.dto.response.InventoryResponse;
import com.ssafy.roCatRun.domain.inventory.dto.response.ItemSellResponse;
import com.ssafy.roCatRun.domain.inventory.entity.Inventory;
import com.ssafy.roCatRun.domain.inventory.repository.InventoryRepository;
import com.ssafy.roCatRun.domain.item.entity.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 인벤토리 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 캐릭터의 아이템 관리와 관련된 모든 기능을 담당
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    /**
     * 캐릭터가 보유한 전체 인벤토리 아이템을 조회합니다.
     */
    public List<InventoryResponse> getInventoryItems(Long characterId) {
        return inventoryRepository.findByGameCharacterId(characterId)
                .stream()
                .map(InventoryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 캐릭터가 보유한 아이템을 카테고리별로 조회합니다.
     */
    public List<InventoryResponse> getInventoryItemsByCategory(Long characterId, String category) {
        return inventoryRepository.findByGameCharacterIdAndItem_Category(
                        characterId,
                        Item.Category.valueOf(category.toUpperCase())
                )
                .stream()
                .map(InventoryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 아이템의 착용 상태를 변경합니다.
     * 아이템을 착용할 경우, 같은 카테고리의 기존 착용 아이템은 자동으로 해제됩니다.
     */
    @Transactional
    public InventoryResponse toggleItem(Long characterId, Long inventoryId) {
        Inventory inventory = validateInventoryAccess(characterId, inventoryId);

        if (!inventory.getIsEquipped()) {
            // 아이템 착용 시 같은 카테고리의 기존 착용 아이템 해제
            inventoryRepository
                    .findByGameCharacterIdAndItem_CategoryAndIsEquippedTrue(
                            characterId,
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
     */
    @Transactional
    public ItemSellResponse sellItem(Long characterId, Long inventoryId) {
        Inventory inventory = validateInventoryAccess(characterId, inventoryId);
        GameCharacter character = inventory.getGameCharacter();

        if (inventory.getIsEquipped()) {
            return null; // 착용중인 아이템은 판매 불가
        }

        int sellPrice = inventory.getItem().getPrice() / 2;  // 판매가는 구매가의 50%
        character.addCoin(sellPrice);

        inventoryRepository.delete(inventory);

        return new ItemSellResponse(inventoryId, sellPrice, character.getCoin());
    }

    /**
     * 인벤토리 아이템 접근을 위한 검증
     */
    private Inventory validateInventoryAccess(Long characterId, Long inventoryId) {
        return inventoryRepository.findById(inventoryId)
                .filter(inventory -> inventory.getGameCharacter().getId().equals(characterId))
                .orElse(null);
    }
}
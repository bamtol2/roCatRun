package com.ssafy.roCatRun.domain.inventory.repository;

import com.ssafy.roCatRun.domain.inventory.entity.Inventory;
import com.ssafy.roCatRun.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // 캐릭터의 전체 인벤토리 조회
    List<Inventory> findByGameCharacterId(Long characterId);

    // 캐릭터의 카테고리별 인벤토리 조회
    List<Inventory> findByGameCharacterIdAndItem_Category(Long characterId, Item.Category category);

    // 캐릭터가 이미 착용중인 같은 카테고리의 아이템이 있는지 확인
    Optional<Inventory> findByGameCharacterIdAndItem_CategoryAndIsEquippedTrue(
            Long characterId,
            Item.Category category
    );
}

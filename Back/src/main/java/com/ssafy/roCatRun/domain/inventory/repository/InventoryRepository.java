package com.ssafy.roCatRun.domain.inventory.repository;

import com.ssafy.roCatRun.domain.inventory.entity.Inventory;
import com.ssafy.roCatRun.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // 회원의 전체 인벤토리 조회
    List<Inventory> findByGameCharacter_Member_Id(Long memberId);

    // 회원의 카테고리별 인벤토리 조회
    List<Inventory> findByGameCharacter_Member_IdAndItem_Category(Long memberId, Item.Category category);

    // 회원이 착용중인 특정 카테고리 아이템 조회
    Optional<Inventory> findByGameCharacter_Member_IdAndItem_CategoryAndIsEquippedTrue(
            Long memberId,
            Item.Category category
    );

    // 인벤토리 아이템 접근 권한 확인
    Optional<Inventory> findByIdAndGameCharacter_Member_Id(Long inventoryId, Long memberId);
}
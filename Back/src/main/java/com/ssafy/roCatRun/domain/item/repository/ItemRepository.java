package com.ssafy.roCatRun.domain.item.repository;

import com.ssafy.roCatRun.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // 카테고리별 아이템 조회
    List<Item> findByCategory(Item.ItemCategory category);

    // 희귀도별 아이템 조회
    List<Item> findByRarity(Item.ItemRarity rarity);
}
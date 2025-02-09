package com.ssafy.roCatRun.domain.item.repository;

import com.ssafy.roCatRun.domain.item.entity.Item;
import com.ssafy.roCatRun.domain.item.entity.UserItem;
import com.ssafy.roCatRun.domain.character.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

/**
 * 사용자의 아이템 정보를 처리하는 Repository
 */
public interface UserItemRepository extends JpaRepository<UserItem, Long> {
    /**
     * 특정 캐릭터가 보유한 모든 아이템 조회
     */
    List<UserItem> findByCharacter(Character character);

    /**
     * 특정 캐릭터가 보유한 아이템을 카테고리별로 조회
     */
    List<UserItem> findByCharacterAndItem_Category(Character character, Item.ItemCategory category);

    /**
     * 특정 캐릭터의 특정 아이템 조회
     */
    Optional<UserItem> findByCharacterAndItem_Id(Character character, Long itemId);
}
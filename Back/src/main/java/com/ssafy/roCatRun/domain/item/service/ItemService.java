package com.ssafy.roCatRun.domain.item.service;

import com.ssafy.roCatRun.domain.item.entity.Item;
import com.ssafy.roCatRun.domain.item.entity.UserItem;
import com.ssafy.roCatRun.domain.item.repository.ItemRepository;
import com.ssafy.roCatRun.domain.item.repository.UserItemRepository;
import com.ssafy.roCatRun.domain.character.entity.Character;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;


    // 뽑기 비용 설정
    private static final int GACHA_COST = 10;

    // 희귀도별 뽑기 확률 (전체 합이 100이 되어야 함)
    private static final Map<Item.ItemRarity, Double> RARITY_PROBABILITIES = Map.of(
            Item.ItemRarity.NORMAL, 50.0,     // 50%
            Item.ItemRarity.RARE, 30.0,       // 30%
            Item.ItemRarity.EPIC, 15.0,       // 15%
            Item.ItemRarity.UNIQUE, 4.0,      // 4%
            Item.ItemRarity.LEGENDARY, 1.0     // 1%
    );

    /**
     * 캐릭터의 모든 아이템 조회
     */
    public List<UserItem> getUserItems(Character character) {
        return userItemRepository.findByCharacter(character);
    }

    /**
     * 카테고리별 아이템 조회
     */
    public List<UserItem> getUserItemsByCategory(Character character, Item.ItemCategory category) {
        return userItemRepository.findByCharacterAndItem_Category(character, category);
    }

    /**
     * 아이템 착용/해제 토글
     */
    @Transactional
    public void toggleEquipItem(Character character, Long itemId) {
        UserItem userItem = userItemRepository.findByCharacterAndItem_Id(character, itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // 착용하려는 경우, 같은 카테고리의 다른 아이템 해제
        if (!userItem.isEquipped()) {
            userItemRepository.findByCharacterAndItem_Category(character, userItem.getItem().getCategory())
                    .stream()
                    .filter(UserItem::isEquipped)
                    .forEach(equipped -> equipped.setEquipped(false));
        }

        userItem.toggleEquipped();
    }

    /**
     * 아이템 판매
     * @return 판매 금액
     */
    @Transactional
    public int sellItem(Character character, Long itemId) {
        UserItem userItem = userItemRepository.findByCharacterAndItem_Id(character, itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // 착용 중인 아이템은 판매 불가
        if (userItem.isEquipped()) {
            throw new RuntimeException("Cannot sell equipped item");
        }

        // 판매 가격 계산
        int sellPrice = userItem.getItem().getSellPrice();

        // 코인 지급
        character.setCoin(character.getCoin() + sellPrice);

        // 아이템 제거
        userItemRepository.delete(userItem);

        return sellPrice;
    }

    /**
     * 아이템 뽑기
     */
    @Transactional
    public Item drawItem(Character character) {
        // 코인 확인
        if (character.getCoin() < GACHA_COST) {
            throw new RuntimeException("Not enough coins");
        }

        // 희귀도 결정
        Item.ItemRarity selectedRarity = selectRarity();

        // 선택된 희귀도의 아이템 중에서 랜덤 선택
        List<Item> items = itemRepository.findByRarity(selectedRarity);
        if (items.isEmpty()) {
            throw new RuntimeException("No items found for selected rarity");
        }

        // 랜덤 아이템 선택
        Random random = new Random();
        Item selectedItem = items.get(random.nextInt(items.size()));

        // 코인 차감
        character.setCoin(character.getCoin() - GACHA_COST);

        // 아이템 지급
        UserItem userItem = new UserItem();
        userItem.setCharacter(character);
        userItem.setItem(selectedItem);
        userItemRepository.save(userItem);

        return selectedItem;
    }

    /**
     * 희귀도 선택 메서드
     */
    private Item.ItemRarity selectRarity() {
        Random random = new Random();
        double value = random.nextDouble() * 100;  // 0-100 사이의 랜덤 값
        double cumulative = 0.0;

        for (Map.Entry<Item.ItemRarity, Double> entry : RARITY_PROBABILITIES.entrySet()) {
            cumulative += entry.getValue();
            if (value <= cumulative) {
                return entry.getKey();
            }
        }

        return Item.ItemRarity.NORMAL;  // 기본값
    }
}
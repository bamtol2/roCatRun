package com.ssafy.roCatRun.domain.item.service;

import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import com.ssafy.roCatRun.domain.gameCharacter.repository.GameCharacterRepository;
import com.ssafy.roCatRun.domain.inventory.entity.Inventory;
import com.ssafy.roCatRun.domain.inventory.repository.InventoryRepository;
import com.ssafy.roCatRun.domain.item.dto.response.ItemDrawResponse;
import com.ssafy.roCatRun.domain.item.entity.Item;
import com.ssafy.roCatRun.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 아이템 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final GameCharacterRepository gameCharacterRepository;
    private final InventoryRepository inventoryRepository;
    private final Random random = new Random();

    /**
     * 아이템 뽑기를 실행하고 결과를 반환합니다.
     * @param memberId 회원 ID
     * @param drawCount 뽑기 횟수
     * @return 뽑기 결과와 남은 코인
     */
    @Transactional
    public ItemDrawResponse drawItem(Long memberId, int drawCount) {
        if (drawCount != 1 && drawCount != 10) {
            return null;  // 클라이언트에서 처리
        }

        GameCharacter character = gameCharacterRepository.findByMemberId(memberId)
                .orElse(null);

        if (character == null) {
            return null;
        }

        // 코인 확인 및 차감
        int requiredCoins = drawCount * 100; // 1회당 100코인
        if (character.getCoin() < requiredCoins) {
            return null;  // 코인 부족
        }

        character.useCoin(requiredCoins);

        // 아이템 뽑기 실행
        List<Item> drawnItems = new ArrayList<>();
        for (int i = 0; i < drawCount; i++) {
            Item item = selectRandomItem();
            drawnItems.add(item);
            // 인벤토리에 아이템 추가
            Inventory inventory = Inventory.createInventory(character, item);
            inventoryRepository.save(inventory);
        }

        // 응답 생성
        List<ItemDrawResponse.DrawnItem> drawnItemResponses = drawnItems.stream()
                .map(ItemDrawResponse.DrawnItem::from)
                .collect(Collectors.toList());

        return new ItemDrawResponse(drawnItemResponses, character.getCoin());
    }

    /**
     * 확률에 따라 랜덤하게 아이템을 선택합니다.
     */
    private Item selectRandomItem() {
        double rand = random.nextDouble();
        Item.ItemRarity rarity;

        if (rand < 0.01) rarity = Item.ItemRarity.LEGENDARY;         // 1%
        else if (rand < 0.05) rarity = Item.ItemRarity.UNIQUE;       // 4%
        else if (rand < 0.25) rarity = Item.ItemRarity.EPIC;         // 20%
        else if (rand < 0.55) rarity = Item.ItemRarity.RARE;         // 30%
        else rarity = Item.ItemRarity.NORMAL;                        // 45%

        List<Item> items = itemRepository.findByRarity(rarity);
        if (items.isEmpty()) {
            // 해당 레어리티의 아이템이 없는 경우 NORMAL 아이템 반환
            items = itemRepository.findByRarity(Item.ItemRarity.NORMAL);
        }

        return items.get(random.nextInt(items.size()));
    }
}
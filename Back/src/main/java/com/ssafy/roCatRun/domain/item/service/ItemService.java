package com.ssafy.roCatRun.domain.item.service;

import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import com.ssafy.roCatRun.domain.gameCharacter.repository.GameCharacterRepository;
import com.ssafy.roCatRun.domain.inventory.entity.Inventory;
import com.ssafy.roCatRun.domain.inventory.repository.InventoryRepository;
import com.ssafy.roCatRun.domain.item.dto.response.ItemDrawResponse;
import com.ssafy.roCatRun.domain.item.entity.Item;
import com.ssafy.roCatRun.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private static final int DRAW_COST = 100; // 1회 뽑기 비용
    private static final String ERROR_INVALID_DRAW_COUNT = "유효하지 않은 뽑기 횟수입니다.";

    private final ItemRepository itemRepository;
    private final GameCharacterRepository gameCharacterRepository;
    private final InventoryRepository inventoryRepository;
    private final Random random = new Random();

    @Transactional
    public ItemDrawResponse drawItem(int drawCount) {
        if (drawCount != 1 && drawCount != 10) {
            throw new IllegalArgumentException(ERROR_INVALID_DRAW_COUNT);
        }

        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        GameCharacter character = gameCharacterRepository.getReferenceById(memberId);

        int requiredCoins = drawCount * DRAW_COST;
        character.useCoin(requiredCoins); // GameCharacter의 useCoin 메서드 사용

        List<Item> drawnItems = new ArrayList<>();
        for (int i = 0; i < drawCount; i++) {
            Item item = selectRandomItem();
            drawnItems.add(item);
            Inventory inventory = Inventory.createInventory(character, item);
            inventoryRepository.save(inventory);
        }

        List<ItemDrawResponse.DrawnItem> drawnItemResponses = drawnItems.stream()
                .map(ItemDrawResponse.DrawnItem::from)
                .collect(Collectors.toList());

        return new ItemDrawResponse(drawnItemResponses, character.getCoin()); // GameCharacter의 getCoin() getter 사용
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
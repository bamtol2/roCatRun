package com.ssafy.roCatRun.domain.inventory.entity;

import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import com.ssafy.roCatRun.domain.item.entity.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 캐릭터의 인벤토리를 관리하는 엔티티
 * - 한 캐릭터가 같은 종류의 아이템을 여러 개 보유할 수 있음
 * - 같은 카테고리의 아이템 중 하나만 착용 가능 (착용 관리는 서비스 계층에서 처리)
 */
@Entity
@Table(name = "inventories")
@Getter @Setter
@NoArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long id;

    /**
     * 캐릭터와 다대일 관계 설정
     * - 한 캐릭터가 여러 인벤토리 항목을 가질 수 있음
     * - 지연 로딩으로 설정하여 성능 최적화
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private GameCharacter gameCharacter;

    /**
     * 아이템과 다대일 관계 설정
     * - 같은 아이템을 여러 번 획득 가능
     * - 초기 인벤토리 생성 시에는 아이템이 없을 수 있으므로 nullable = true
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = true)
    private Item item;

    /**
     * 아이템 착용 상태
     * - true: 착용 중
     * - false: 미착용
     */
    @Column(nullable = false)
    private Boolean isEquipped = false;

    /**
     * 초기 인벤토리 생성을 위한 팩토리 메서드
     * - 캐릭터 생성 시 함께 생성되는 빈 인벤토리
     *
     * @param gameCharacter 인벤토리를 소유할 캐릭터
     * @return 생성된 빈 인벤토리
     */
    public static Inventory createInitialInventory(GameCharacter gameCharacter) {
        Inventory inventory = new Inventory();
        inventory.setGameCharacter(gameCharacter);
        inventory.setItem(null);
        inventory.setIsEquipped(false);
        return inventory;
    }

    /**
     * 아이템이 있는 인벤토리 생성을 위한 팩토리 메서드
     * - 아이템 획득 시 사용
     *
     * @param gameCharacter 인벤토리를 소유할 캐릭터
     * @param item 저장할 아이템
     * @return 생성된 인벤토리
     */
    public static Inventory createInventory(GameCharacter gameCharacter, Item item) {
        Inventory inventory = new Inventory();
        inventory.setGameCharacter(gameCharacter);
        inventory.setItem(item);
        inventory.setIsEquipped(false);
        return inventory;
    }

    /**
     * 아이템 착용 상태를 전환하는 메서드
     * - 이미 착용 중인 경우: 착용 해제
     * - 미착용 상태인 경우: 같은 카테고리의 다른 착용 아이템을 해제하고 착용
     */
    public void toggleEquipped() {
        if (this.isEquipped) {
            this.isEquipped = false;
            return;
        }

        this.gameCharacter.unequipCategoryItems(this.getItem().getCategory());
        this.isEquipped = true;
    }

    /**
     * 아이템의 카테고리를 반환하는 메서드
     * - 아이템이 없는 경우 null 반환
     *
     * @return 아이템의 카테고리 또는 null
     */
    public Item.Category getCategory() {
        return item != null ? item.getCategory() : null;
    }
}
package com.ssafy.roCatRun.domain.inventory.entity;

import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import com.ssafy.roCatRun.domain.item.entity.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 캐릭터의 인벤토리를 관리하는 엔티티
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", unique = true)
    private GameCharacter gameCharacter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private Boolean isEquipped = false;

    /**
     * 새로운 인벤토리를 생성하는 팩토리 메서드
     */
    public static Inventory createInventory(GameCharacter gameCharacter, Item item) {
        Inventory inventory = new Inventory();
        inventory.setGameCharacter(gameCharacter);
        inventory.setItem(item);
        return inventory;
    }

    /**
     * 아이템 착용 상태를 전환하는 메서드
     */
    public void toggleEquipped() {
        this.isEquipped = !this.isEquipped;
    }
}
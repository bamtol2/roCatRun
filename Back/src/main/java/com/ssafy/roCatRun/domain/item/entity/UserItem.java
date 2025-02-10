package com.ssafy.roCatRun.domain.item.entity;

import com.ssafy.roCatRun.domain.character.entity.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_items")
@Getter @Setter
@NoArgsConstructor
public class UserItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Character character;    // 소유 캐릭터

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;             // 아이템 정보

    @Column(nullable = false)
    private boolean isEquipped = false;  // 착용 여부

    // 아이템 착용 상태 토글 메서드
    public void toggleEquipped() {
        this.isEquipped = !this.isEquipped;
    }
    public void setEquipped(boolean equipped) {
        this.isEquipped = equipped;
    }
}
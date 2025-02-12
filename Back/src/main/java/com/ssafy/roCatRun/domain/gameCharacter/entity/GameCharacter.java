package com.ssafy.roCatRun.domain.gameCharacter.entity;

import com.ssafy.roCatRun.domain.inventory.entity.Inventory;
import com.ssafy.roCatRun.domain.item.entity.Item;
import com.ssafy.roCatRun.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임 캐릭터 정보를 저장하는 엔티티
 * 사용자의 게임 캐릭터 정보와 관련된 속성들을 관리
 */
@Entity
@Table(
        name = "game_characters",
        indexes = {
                @Index(
                        name = "idx_character_level_exp",
                        columnList = "level DESC, experience DESC"
                )
        }
)
@Getter @Setter
@NoArgsConstructor
public class GameCharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String nickname;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer level = 1;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer experience = 0;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'default.png'")
    private String characterImage = "default.png";

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer coin = 0;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalGames = 0;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer wins = 0;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer losses = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            referencedColumnName = "member_id",
            unique = true
    )
    private Member member;

    // 인벤토리와의 양방향 관계 추가
    @OneToMany(mappedBy = "gameCharacter", cascade = CascadeType.ALL)
    private List<Inventory> inventories = new ArrayList<>();

    // 캐릭터 생성 팩토리 메서드
    public static GameCharacter createCharacter(String nickname, Member member) {
        GameCharacter gameCharacter = new GameCharacter();
        gameCharacter.setNickname(nickname);
        gameCharacter.setMember(member);
        member.setGameCharacter(gameCharacter);

        // 초기 인벤토리 생성
        Inventory initialInventory = Inventory.createInitialInventory(gameCharacter);
        gameCharacter.getInventories().add(initialInventory);

        return gameCharacter;
    }

    // 같은 카테고리의 장착 아이템 해제
    public void unequipCategoryItems(Item.Category category) {
        this.inventories.stream()
                .filter(inv -> inv.getCategory() == category && inv.getIsEquipped())
                .forEach(inv -> inv.setIsEquipped(false));
    }

    // 기타 메서드들...
    public void addExperience(int exp) {
        this.experience += exp;
        checkLevelUp();
    }

    private void checkLevelUp() {
        int experienceForLevel = 100;
        while (this.experience >= experienceForLevel) {
            this.level += 1;
            this.experience -= experienceForLevel;
        }
    }

    public void addCoin(int amount) {
        this.coin += amount;
    }

    public void useCoin(int amount) {
        if (this.coin < amount) {
            throw new IllegalArgumentException("코인이 부족합니다.");
        }
        this.coin -= amount;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }
}
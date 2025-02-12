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
    private Long id;                           // 캐릭터 고유 ID

    @Column(nullable = false, unique = true, length = 10)
    private String nickname;                   // 캐릭터 닉네임

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer level = 1;                 // 캐릭터 레벨 (기본값 1)

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer experience = 0;            // 현재 보유 경험치 (기본값 0)

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'default.png'")
    private String characterImage = "default.png";  // 캐릭터 이미지 (기본값 'default.png')

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer coin = 0;                  // 보유 코인 (기본값 0)

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalGames = 0;            // 총 게임 수 (기본값 0)

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer wins = 0;                  // 승리 수 (기본값 0)

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer losses = 0;                // 패배 수 (기본값 0)

    // 회원과의 일대일 관계 설정
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            referencedColumnName = "member_id",
            unique = true
    )
    private Member member;

    // 인벤토리와의 일대다 관계 설정
    @OneToMany(mappedBy = "gameCharacter", cascade = CascadeType.ALL)
    private List<Inventory> inventories = new ArrayList<>();

    /**
     * 새로운 게임 캐릭터를 생성하는 팩토리 메서드
     * @param nickname 캐릭터 닉네임
     * @param member 연결된 회원 정보
     * @return 생성된 GameCharacter 객체
     */
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

    /**
     * 경험치를 추가하고 레벨업 체크를 수행하는 메서드
     * @param exp 추가할 경험치량
     */
    public void addExperience(int exp) {
        this.experience += exp;
        checkLevelUp();
    }

    /**
     * 현재 경험치를 기반으로 레벨업 조건을 체크하고 처리하는 메서드
     * Level 엔티티와 연동되어 처리됨
     */
    private void checkLevelUp() {
        // 이 메서드는 나중에 Level 엔티티와 연동하여 구현될 예정
        int experienceForLevel = 100;
        while (this.experience >= experienceForLevel) {
            this.level += 1;
            this.experience -= experienceForLevel;
        }
    }

    /**
     * 코인을 추가하는 메서드
     * @param amount 추가할 코인량
     */
    public void addCoin(int amount) {
        this.coin += amount;
    }

    /**
     * 코인을 사용하는 메서드
     * @param amount 사용할 코인량
     * @throws IllegalArgumentException 보유 코인이 부족한 경우
     */
    public void useCoin(int amount) {
        if (this.coin < amount) {
            throw new IllegalArgumentException("코인이 부족합니다.");
        }
        this.coin -= amount;
    }

    /**
     * 캐릭터의 닉네임을 업데이트하는 메서드
     * @param newNickname 새로운 닉네임
     */
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    /**
     * 동일 카테고리의 장착된 아이템을 해제하는 메서드
     * @param category 해제할 아이템 카테고리
     */
    public void unequipCategoryItems(Item.Category category) {
        this.inventories.stream()
                .filter(inv -> inv.getCategory() == category && inv.getIsEquipped())
                .forEach(inv -> inv.setIsEquipped(false));
    }
}
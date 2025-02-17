package com.ssafy.roCatRun.domain.gameCharacter.entity;

import com.ssafy.roCatRun.domain.game.entity.raid.GameResult;
import com.ssafy.roCatRun.domain.inventory.entity.Inventory;
import com.ssafy.roCatRun.domain.item.entity.Item;
import com.ssafy.roCatRun.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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

    @Column(nullable = false, unique = true, length = 8)
    private String nickname;                   // 캐릭터 닉네임

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level", referencedColumnName = "level")
    private Level levelInfo;                    // level 엔티티와 관계 추가

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer experience = 0;            // 현재 보유 경험치 (기본값 0)

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'default.png'")
    private String characterImage = "default.png";  // 캐릭터 이미지 (기본값 'default.png')

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer coin = 100;                  // 보유 코인 (기본값 0)

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalGames = 0;            // 총 게임 수 (기본값 0)

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer wins = 0;                  // 승리 수 (기본값 0)

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer losses = 0;                // 패배 수 (기본값 0)

    /**
     * Member와의 일대일 관계 설정
     * Member가 삭제될 때 GameCharacter도 함께 삭제됨
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            referencedColumnName = "member_id",
            unique = true
    )
    private Member member;

    /**
     * GameResult와의 일대다 관계 설정
     * GameCharacter가 삭제될 때 관련된 모든 GameResult도 함께 삭제
     */
    @OneToMany(
            mappedBy = "character",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<GameResult> gameResults = new ArrayList<>();

    /**
     * Inventory와의 일대다 관계 설정
     * GameCharacter가 삭제될 때 관련된 모든 Inventory도 함께 삭제
     */
    @OneToMany(
            mappedBy = "gameCharacter",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
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

        // 초기 레벨(1) 설정
        Level initialLevel = Level.createLevel(1, 500);
        gameCharacter.setLevelInfo(initialLevel);

        // 초기 인벤토리 생성
        Inventory initialInventory = Inventory.createInitialInventory(gameCharacter);
        gameCharacter.getInventories().add(initialInventory);


        return gameCharacter;
    }

    /**
     * 경험치를 추가하고 레벨업 체크를 수행하는 메서드
     * @param exp 추가할 경험치량
     * @return 레벨업 결과 정보
     */
    public void addExperience(int exp) {
        int oldLevel = this.levelInfo.getLevel();
        this.experience += exp;
    }

//    /**
//     * 현재 경험치를 기반으로 레벨업 조건을 체크하고 처리하는 메서드
//     * Level 엔티티와 연동되어 처리됨
//     */
//    private void checkLevelUp() {
//        // 현재 레벨의 필요 경험치와 비교
//        while(this.experience >= this.levelInfo.getRequiredExp()){
//            // 현재 레벨의 필요 경험치만큼 차감
//            this.experience -= this.levelInfo.getRequiredExp();
//
//            this.level++;// 레벨 증가
//        }
//    }

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

    // GameCharacter.java에 추가할 메서드

    /**
     * 게임 결과를 반영하여 통계를 업데이트하는 메서드
     * @param isCleared 보스 클리어 여부
     */
    public void updateGameStats(boolean isCleared) {
        this.totalGames++;
        if (isCleared) {
            this.wins++;
        } else {
            this.losses++;
        }
    }

    /**
     * 현재 승률을 계산하는 메서드
     * @return 승률 (퍼센트)
     */
    public double getWinRate() {
        if (totalGames == 0) return 0.0;
        return (double) wins / totalGames * 100;
    }

    @Getter
    @AllArgsConstructor
    public static class LevelUpResult {
        private final int oldLevel;
        private final int currentExp;
    }
}
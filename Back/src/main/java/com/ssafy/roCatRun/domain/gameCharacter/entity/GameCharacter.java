package com.ssafy.roCatRun.domain.gameCharacter.entity;

import com.ssafy.roCatRun.domain.inventory.entity.Inventory;
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
                        columnList = "level DESC, experience DESC"  // 랭킹 시스템을 위한 복합 인덱스
                )
        }
)
@Getter @Setter
@NoArgsConstructor
public class GameCharacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Long id;                // 캐릭터의 고유 식별자

    @Column(nullable = false, unique = true, length = 10)
    private String nickname;        // 캐릭터 닉네임 (최대 10자, 중복 불가)

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer level = 1;      // 캐릭터 레벨 (기본값 1)

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer experience = 0; // 경험치 (기본값 0)

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'default.png'")
    private String characterImage = "default.png";  // 캐릭터 이미지 경로

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer coin = 0;       // 보유 코인 수 (기본값 0)

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalGames = 0;    // 총 게임 수

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer wins = 0;          // 승리 수

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer losses = 0;        // 패배 수

    /**
     * Member 엔티티와의 일대일 관계 설정
     * - FetchType.LAZY: 성능 최적화를 위해 필요할 때만 Member 정보를 조회
     * - JoinColumn: member_id를 외래키로 사용하며, unique 제약조건으로 1:1 관계 보장
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",            // 외래키 컬럼명
            referencedColumnName = "member_id",  // 참조하는 Member 엔티티의 PK 컬럼명
            unique = true                  // 1:1 관계 보장을 위한 unique 제약조건
    )
    private Member member;

    /**
     * Inventory 엔티티와의 일대다 관계 설정
     * - mappedBy: Inventory 엔티티의 gameCharacter 필드와 매핑
     * - cascade: GameCharacter 엔티티의 변경사항이 Inventory 엔티티에도 적용
     * - orphanRemoval: 연관관계가 끊어진 Inventory 엔티티 자동 삭제
     */
    @OneToMany(mappedBy = "gameCharacter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inventory> inventories = new ArrayList<>();

    /**
     * 인벤토리에 아이템 추가
     * @param inventory 추가할 인벤토리 아이템
     */
    public void addInventory(Inventory inventory) {
        inventories.add(inventory);
        inventory.setGameCharacter(this);
    }

    /**
     * 인벤토리에서 아이템 제거
     * @param inventory 제거할 인벤토리 아이템
     */
    public void removeInventory(Inventory inventory) {
        inventories.remove(inventory);
        inventory.setGameCharacter(null);
    }

    /**
     * 새로운 캐릭터를 생성하는 팩토리 메서드
     * 캐릭터 생성과 Member와의 연관관계 설정을 한 번에 처리
     *
     * @param nickname 캐릭터 닉네임
     * @param member 연결할 Member 엔티티
     * @return 생성된 Character 엔티티
     */
    public static GameCharacter createCharacter(String nickname, Member member) {
        GameCharacter gameCharacter = new GameCharacter();
        gameCharacter.setNickname(nickname);
        gameCharacter.setMember(member);        // 멤버 연결
        member.setGameCharacter(gameCharacter);     // 양방향 관계 설정
        return gameCharacter;
    }

    /**
     * 경험치를 추가하고 레벨업 조건을 확인하는 메서드
     *
     * @param exp 추가할 경험치량
     */
    public void addExperience(int exp) {
        this.experience += exp;
        checkLevelUp();             // 경험치 추가 후 레벨업 조건 확인
    }

    /**
     * 레벨업 조건을 확인하고 필요한 경우 레벨을 증가시키는 private 메서드
     * 현재는 간단한 레벨업 로직만 구현 (100 경험치당 1레벨)
     */
    private void checkLevelUp() {
        int experienceForLevel = 100;  // 레벨당 필요 경험치
        while (this.experience >= experienceForLevel) {
            this.level += 1;
            this.experience -= experienceForLevel;
        }
    }

    /**
     * 코인을 추가하는 메서드
     *
     * @param amount 추가할 코인 수량
     */
    public void addCoin(int amount) {
        this.coin += amount;
    }

    /**
     * 코인을 사용하는 메서드
     *
     * @param amount 사용할 코인 수량
     * @throws IllegalArgumentException 보유 코인이 부족한 경우
     */
    public void useCoin(int amount) {
        if (this.coin < amount) {
            throw new IllegalArgumentException("코인이 부족합니다.");
        }
        this.coin -= amount;
    }

    /**
     * 캐릭터의 닉네임을 변경하는 메서드
     * @param newNickname 새로운 닉네임
     */
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }
}
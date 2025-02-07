package com.ssafy.roCatRun.domain.character.entity;

import com.ssafy.roCatRun.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "characters")
@Getter @Setter
@NoArgsConstructor
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")  // PK 컬럼명 명시
    private Long id;

    @Column(nullable = false, unique = true, length = 10)  // 닉네임 최대 길이 10자로 제한
    private String nickname;        // 캐릭터 닉네임 (중복 불가)

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer level = 1;      // 캐릭터 레벨 (기본값 1)

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer experience = 0; // 경험치 (기본값 0)

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'default.png'")
    private String characterImage = "default.png";  // 캐릭터 이미지 경로 (기본값 설정)

    // Member와의 일대일 관계 설정
    // FetchType.LAZY로 설정하여 필요할 때만 Member 정보를 조회
    // member_id를 외래키로 사용
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", unique = true)  // unique 제약조건으로 1:1 관계 보장
    private Member member;

    // 캐릭터 생성 메서드
    public static Character createCharacter(String nickname, Member member) {
        Character character = new Character();
        character.setNickname(nickname);
        character.setMember(member);        // 멤버 연결
        member.setCharacter(character);     // 양방향 관계 설정
        return character;
    }
}
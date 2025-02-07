package com.ssafy.roCatRun.domain.member.entity;

import com.ssafy.roCatRun.domain.character.entity.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter @Setter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")  // PK 컬럼명 명시
    private Long id;

    @Column(nullable = false)
    private String loginType;    // 소셜 로그인 타입 (KAKAO, NAVER, GOOGLE)

    @Column(nullable = false)
    private String name;         // 소셜 로그인에서 받아온 이름

    @Column(nullable = false, unique = true)  // socialId 유니크 제약 추가
    private String socialId;     // 소셜 서비스의 고유 ID

    @Column(nullable = true)
    private String email;        // 소셜 로그인에서 받아온 이메일 (선택적)

    @Column(nullable = false, updatable = false)  // 생성일자는 수정 불가
    private LocalDateTime createAt;    // 회원 가입 시간

    @Column(nullable = false)
    private LocalDateTime lastLoginAt; // 마지막 로그인 시간

    // 회원과 캐릭터의 1:1 관계 설정
    // mappedBy는 Character 엔티티의 member 필드를 참조
    // CascadeType.ALL로 설정하여 회원 삭제 시 캐릭터도 함께 삭제
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Character character;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createAt == null) {
            createAt = now;
        }
        if (lastLoginAt == null) {
            lastLoginAt = now;
        }
    }

    public void updateLastLoginAt(){
        this.lastLoginAt = LocalDateTime.now();
    }

    // 회원 생성 메서드
    public static Member createMember(String email, String nickname, String loginType, String socialId) {
        Member member = new Member();
        member.setName(nickname);
        member.setLoginType(loginType);
        member.setSocialId(socialId);
        member.setEmail(email);
        return member;
    }
}
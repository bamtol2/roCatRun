package com.ssafy.roCatRun.domain.member.entity;

import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 회원 정보를 저장하는 엔티티
 * 소셜 로그인으로 가입한 회원의 정보와 캐릭터 정보를 관리
 */
@Entity
@Table(name = "members")  // 테이블 이름을 'members'로 지정
@Getter @Setter          // Lombok을 사용하여 getter/setter 자동 생성
@NoArgsConstructor       // 기본 생성자 자동 생성
public class Member {
    @Id  // Primary Key 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // auto increment 설정
    @Column(name = "member_id")  // 컬럼명을 'member_id'로 지정
    private Long id;

    @Column(nullable = false)  // NOT NULL 제약조건
    private String loginType;  // 소셜 로그인 타입 (KAKAO, NAVER, GOOGLE)

    @Column(nullable = false)
    private String name;      // 소셜 로그인에서 받아온 이름

    @Column(nullable = false, unique = true)  // NULL 불가, 중복 불가 제약조건
    private String socialId;  // 소셜 서비스에서 제공하는 고유 ID

    @Column(nullable = true)
    private String email;     // 소셜 로그인에서 받아온 이메일 (선택적)

    @Column(nullable = false, updatable = false)  // 생성 후 수정 불가
    private LocalDateTime createAt;    // 회원 가입 시간

    @Column(nullable = false)
    private LocalDateTime lastLoginAt; // 마지막 로그인 시간

    // 사용자의 신체 정보 (선택적)
    @Column(nullable = true)
    private Integer height;   // 키

    @Column(nullable = true)
    private Integer weight;   // 몸무게

    @Column(nullable = true)
    private Integer age;      // 나이

    @Column(nullable = true)
    private String gender;    // 성별

    /**
     * Member와 Character 간의 1:1 관계 설정
     * mappedBy: Character 엔티티의 member 필드와 매핑
     * cascade: Member 엔티티의 변경사항이 Character 엔티티에도 적용
     */
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private GameCharacter gameCharacter;

    /**
     * 엔티티 생성 시 자동으로 호출되는 메서드
     * 최초 생성 시간과 마지막 로그인 시간을 현재 시간으로 설정
     */
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

    /**
     * 마지막 로그인 시간을 현재 시간으로 업데이트
     */
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * 새로운 Member 엔티티를 생성하는 정적 팩토리 메서드
     *
     * @param email 사용자 이메일
     * @param name 사용자 닉네임
     * @param loginType 소셜 로그인 타입
     * @param socialId 소셜 서비스의 고유 ID
     * @return 생성된 Member 엔티티
     */
    public static Member createMember(String email, String name, String loginType, String socialId) {
        Member member = new Member();
        member.setName(name);
        member.setLoginType(loginType);
        member.setSocialId(socialId);
        member.setEmail(email);
        return member;
    }

    /**
     * 회원의 캐릭터 정보를 반환
     * 캐릭터가 없는 경우 예외 발생
     *
     * @return Character 회원의 캐릭터 정보
     * @throws RuntimeException 캐릭터가 존재하지 않는 경우
     */
    public GameCharacter getGameCharacter() {
        if (this.gameCharacter == null) {
            throw new RuntimeException("Character not found for member");
        }
        return this.gameCharacter;
    }

    /**
     * 소셜 로그인 타입을 반환
     * @return 소셜 로그인 타입 (KAKAO, NAVER, GOOGLE)
     */
    public String getSocialType() {
        return this.loginType;
    }
}
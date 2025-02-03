package com.ssafy.roCatRun.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "members")
@Getter @Setter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long socialId;

    @Column(nullable = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String loginType;

    public static Member createMember(String email, String nickname, String loginType, Long socialId) {
        Member member = new Member();
        member.setEmail(email);
        member.setNickname(nickname);
        member.setLoginType(loginType);
        member.setSocialId(socialId);
        return member;
    }
}
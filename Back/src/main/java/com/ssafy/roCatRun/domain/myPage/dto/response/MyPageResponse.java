package com.ssafy.roCatRun.domain.myPage.dto.response;
import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import com.ssafy.roCatRun.domain.member.entity.Member;
import com.ssafy.roCatRun.global.common.ApiResponse;
import lombok.Getter;

@Getter
public class MyPageResponse {
    private final String nickname;        // 캐릭터 닉네임
    private final String socialType;      // 소셜 로그인 타입
    private final Integer height;         // 키
    private final Integer weight;         // 몸무게
    private final Integer age;            // 나이
    private final String gender;          // 성별

    public MyPageResponse(Member member, GameCharacter character) {
        this.nickname = character.getNickname();
        this.socialType = member.getSocialType();
        this.height = member.getHeight();
        this.weight = member.getWeight();
        this.age = member.getAge();
        this.gender = member.getGender();
    }
}

package com.ssafy.roCatRun.domain.member.service;

import com.ssafy.roCatRun.domain.gameCharacter.repository.GameCharacterRepository;
import com.ssafy.roCatRun.domain.member.dto.request.MemberProfileUpdateRequest;
import com.ssafy.roCatRun.domain.member.entity.Member;
import com.ssafy.roCatRun.domain.member.repository.MemberRepository;
import com.ssafy.roCatRun.domain.member.repository.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GameCharacterRepository gameCharacterRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final RestTemplate restTemplate;

    /**
     * 회원 탈퇴 처리
     * - Member 엔티티 삭제 시 CascadeType.ALL 설정으로 인해 연관된 엔티티들이 자동 삭제됨:
     *   - GameCharacter (orphanRemoval = true)
     *   - GameResult (CascadeType.ALL)
     *   - Inventory (CascadeType.ALL)
     * - Redis에 저장된 토큰 정보 삭제
     */
    @Transactional
    public void deleteMember(Long memberId) {
        // 리프레시 토큰 삭제
        refreshTokenRedisRepository.deleteByKey(memberId.toString());

        // 회원 삭제 (연관된 엔티티들은 cascade로 자동 삭제)
        memberRepository.deleteById(memberId);
    }

    @Transactional
    public void logout(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 소셜 로그인 타입에 따라 로그아웃 처리
        String loginType = member.getLoginType();
        if ("KAKAO".equals(loginType)) {
            logoutFromKakao(memberId);
        } else if ("GOOGLE".equals(loginType)) {
            logoutFromGoogle(memberId);
        }

        // JWT 리프레시 토큰 삭제
        refreshTokenRedisRepository.deleteByKey(memberId.toString());

        // 소셜 토큰들 삭제
        refreshTokenRedisRepository.deleteByKey(loginType + "_ACCESS_" + memberId);
        refreshTokenRedisRepository.deleteByKey(loginType + "_REFRESH_" + memberId);
    }

    /**
     * 카카오 서버에 로그아웃 요청
     * 카카오 액세스 토큰을 무효화하지만, 사용자의 동의 정보는 유지됨
     */
    private void logoutFromKakao(Long memberId) {
        try {
            String accessToken = refreshTokenRedisRepository.findByKey("KAKAO_ACCESS_" + memberId)
                    .orElseThrow(() -> new IllegalArgumentException("카카오 액세스 토큰이 없습니다."));

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            restTemplate.postForEntity(
                    "https://kapi.kakao.com/v1/user/unlink",
                    entity,
                    String.class
            );
        } catch (Exception e) {
            // 로그아웃 실패 시에도 토큰은 삭제되어야 함
            log.error("카카오 로그아웃 실패", e);
        }
    }

    /**
     * 구글 서버에 로그아웃 요청
     * 구글 액세스 토큰을 무효화하지만, 사용자의 동의 정보는 유지됨
     */
    private void logoutFromGoogle(Long memberId) {
        try {
            String accessToken = refreshTokenRedisRepository.findByKey("GOOGLE_ACCESS_" + memberId)
                    .orElseThrow(() -> new IllegalArgumentException("구글 액세스 토큰이 없습니다."));

            String revokeUrl = "https://oauth2.googleapis.com/revoke?token=" + accessToken;

            restTemplate.postForEntity(
                    revokeUrl,
                    null,
                    String.class
            );
        } catch (Exception e) {
            // 로그아웃 실패 시에도 토큰은 삭제되어야 함
            log.error("구글 로그아웃 실패", e);
        }
    }


    @Transactional
    public void updateMemberProfile(Long memberId, MemberProfileUpdateRequest request) {
        Member member = memberRepository.findByIdWithCharacter(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // null이 아닌 필드만 업데이트
        if (request.getHeight() != null) {
            member.setHeight(request.getHeight());
        }
        if (request.getWeight() != null) {
            member.setWeight(request.getWeight());
        }
        if (request.getAge() != null) {
            member.setAge(request.getAge());
        }
        if (request.getGender() != null) {
            member.setGender(request.getGender());
        }
    }
}

package com.ssafy.roCatRun.domain.member.service.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.roCatRun.domain.member.dto.response.LoginResponse;
import com.ssafy.roCatRun.domain.member.dto.token.JwtTokens;
import com.ssafy.roCatRun.domain.member.entity.Member;
import com.ssafy.roCatRun.domain.member.repository.MemberRepository;
import com.ssafy.roCatRun.domain.member.repository.RefreshTokenRedisRepository;
import com.ssafy.roCatRun.global.exception.TokenRefreshException;
import com.ssafy.roCatRun.global.security.jwt.JwtTokenGenerator;
import com.ssafy.roCatRun.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NaverService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenGenerator jwtTokensGenerator;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Value("${oauth2.naver.client_id}")
    private String clientId;

    @Value("${oauth2.naver.client_secret}")
    private String clientSecret;

    @Value("${oauth2.naver.redirect_uri}")
    private String defaultRedirectUri;

    private static final long TOKEN_EXPIRATION_TIME_MS = 1000 * 60 * 60 * 24 * 14;

    private String selectRedirectUri(String currentDomain) {
        if (currentDomain.contains("localhost")) {
            return defaultRedirectUri;
        } else {
            return defaultRedirectUri.replace("localhost:8080", currentDomain);
        }
    }

    public LoginResponse naverLogin(String code, String state, String currentDomain) {
        log.info("인가 코드: {}", code);

        String redirectUri = selectRedirectUri(currentDomain);
        // 네이버 토큰 정보 받아오기
        NaverTokenInfo naverTokenInfo = getNaverTokens(code, state, redirectUri);
        log.info("네이버 액세스 토큰: {}", naverTokenInfo.accessToken);

        // 네이버 사용자 정보 조회
        HashMap<String, Object> userInfo = getNaverUserInfo(naverTokenInfo.accessToken);
        log.info("네이버 사용자 정보: {}", userInfo);

        return processNaverLogin(userInfo, naverTokenInfo);
    }

    public JwtTokens refreshNaverToken(String refreshToken) {
        log.info("=================== Token Refresh Start ===================");

        String userId = jwtTokenProvider.extractSubject(refreshToken);
        String naverRefreshToken = refreshTokenRedisRepository.findByKey("NAVER_" + userId)
                .orElseThrow(() -> new TokenRefreshException("저장된 네이버 리프레시 토큰이 없습니다."));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", naverRefreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                request,
                String.class
        );

        JsonNode jsonNode = parseJsonResponse(response.getBody());
        String newNaverAccessToken = jsonNode.get("access_token").asText();
        String newNaverRefreshToken = jsonNode.has("refresh_token")
                ? jsonNode.get("refresh_token").asText()
                : naverRefreshToken;

        JwtTokens newJwtTokens = jwtTokensGenerator.generate(userId);
        log.info("Generated new JWT tokens: Access={}, Refresh={}",
                newJwtTokens.getAccessToken(),
                newJwtTokens.getRefreshToken()
        );

        if (!naverRefreshToken.equals(newNaverRefreshToken)) {
            refreshTokenRedisRepository.save("NAVER_" + userId, newNaverRefreshToken, TOKEN_EXPIRATION_TIME_MS);
        }
        refreshTokenRedisRepository.save(userId, newJwtTokens.getRefreshToken(), TOKEN_EXPIRATION_TIME_MS);

        return newJwtTokens;
    }

    private static class NaverTokenInfo {
        String accessToken;
        String refreshToken;

        NaverTokenInfo(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    private NaverTokenInfo getNaverTokens(String code, String state, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        body.add("state", state);

        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        JsonNode jsonNode = parseJsonResponse(response.getBody());
        return new NaverTokenInfo(
                jsonNode.get("access_token").asText(),
                jsonNode.get("refresh_token").asText()
        );
    }

    private HashMap<String, Object> getNaverUserInfo(String accessToken) {
        HashMap<String, Object> userInfo = new HashMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverUserInfoRequest,
                String.class
        );

        JsonNode jsonNode = parseJsonResponse(response.getBody());
        JsonNode responseNode = jsonNode.get("response");

        String id = responseNode.get("id").asText();
        String nickname = responseNode.get("nickname").asText();

        userInfo.put("id", id);
        userInfo.put("nickname", nickname);

        return userInfo;
    }

    private JsonNode parseJsonResponse(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패", e);
            throw new RuntimeException("JSON 파싱 실패", e);
        }
    }

    private LoginResponse processNaverLogin(HashMap<String, Object> userInfo, NaverTokenInfo naverTokenInfo) {
        // socialId 타입 통일
        String socialId = userInfo.get("id").toString();
        String nickname = userInfo.get("nickname").toString();

        Member member = memberRepository.findBySocialIdAndLoginType(socialId, "NAVER")
                .orElseGet(() -> {
                    Member newMember = Member.createMember(null, nickname, "NAVER", socialId);
                    return memberRepository.save(newMember);
                });

        JwtTokens jwtTokens = jwtTokensGenerator.generate(member.getId().toString());
        log.info("Generated JWT tokens for user {}: Access={}, Refresh={}",
                member.getId(),
                jwtTokens.getAccessToken(),
                jwtTokens.getRefreshToken()
        );

        // Redis에 토큰 저장
        refreshTokenRedisRepository.save(
                member.getId().toString(),
                jwtTokens.getRefreshToken(),
                TOKEN_EXPIRATION_TIME_MS
        );
        refreshTokenRedisRepository.save(
                "NAVER_" + member.getId().toString(),
                naverTokenInfo.refreshToken,
                TOKEN_EXPIRATION_TIME_MS
        );

        return new LoginResponse(jwtTokens);
    }
}
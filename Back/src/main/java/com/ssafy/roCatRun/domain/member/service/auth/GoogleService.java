package com.ssafy.roCatRun.domain.member.service.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.roCatRun.domain.member.dto.oauth.GoogleLoginDto;
import com.ssafy.roCatRun.domain.member.dto.response.LoginResponse;
import com.ssafy.roCatRun.domain.member.dto.token.JwtTokens;
import com.ssafy.roCatRun.domain.member.dto.userinfo.GoogleUserInfoResponseDto;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GoogleService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenGenerator jwtTokensGenerator;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Value("${oauth2.google.client_id}")
    private String clientId;

    @Value("${oauth2.google.client_secret}")
    private String clientSecret;

    @Value("${oauth2.google.redirect_uri}")
    private String defaultRedirectUri;

    private static final long TOKEN_EXPIRATION_TIME_MS = 1000 * 60 * 60 * 24 * 14;

    private String selectRedirectUri(String currentDomain) {
        if (currentDomain.contains("localhost")) {
            return defaultRedirectUri;
        } else {
            return defaultRedirectUri.replace("localhost:8080", currentDomain);
        }
    }

    public LoginResponse googleLogin(String code, String currentDomain) {
        log.info("인가 코드: {}", code);

        String redirectUri = selectRedirectUri(currentDomain);
        log.info("Redirect URI: {}", redirectUri);

        // 구글 토큰 정보 받아오기
        GoogleLoginDto.TokenResponse tokenInfo = getGoogleTokens(code, redirectUri);
        log.info("구글 액세스 토큰: {}", tokenInfo.getAccess_token());

        // 구글 사용자 정보 조회
        GoogleUserInfoResponseDto userInfo = getGoogleUserInfo(tokenInfo.getAccess_token());
        log.info("구글 사용자 정보: {}", userInfo);

        return processGoogleLogin(userInfo, tokenInfo);
    }

    public JwtTokens refreshGoogleToken(String refreshToken) {
        log.info("=================== Token Refresh Start ===================");

        String userId = jwtTokenProvider.extractSubject(refreshToken);
        String googleRefreshToken = refreshTokenRedisRepository.findByKey("GOOGLE_" + userId)
                .orElseThrow(() -> new TokenRefreshException("저장된 구글 리프레시 토큰이 없습니다."));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", googleRefreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                request,
                String.class
        );

        GoogleLoginDto.TokenResponse tokenInfo = parseResponse(response.getBody(), GoogleLoginDto.TokenResponse.class);
        JwtTokens newJwtTokens = jwtTokensGenerator.generate(userId);

        String newGoogleRefreshToken = tokenInfo.getRefresh_token() != null ?
                tokenInfo.getRefresh_token() : googleRefreshToken;

        if (!googleRefreshToken.equals(newGoogleRefreshToken)) {
            refreshTokenRedisRepository.save("GOOGLE_" + userId, newGoogleRefreshToken, TOKEN_EXPIRATION_TIME_MS);
        }
        refreshTokenRedisRepository.save(userId, newJwtTokens.getRefreshToken(), TOKEN_EXPIRATION_TIME_MS);

        return newJwtTokens;
    }

    private GoogleLoginDto.TokenResponse getGoogleTokens(String code, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                googleTokenRequest,
                String.class
        );

        log.info("Google Token Response: {}", response.getBody());
        return parseResponse(response.getBody(), GoogleLoginDto.TokenResponse.class);
    }

    private GoogleUserInfoResponseDto getGoogleUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<MultiValueMap<String, String>> googleUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                googleUserInfoRequest,
                String.class
        );

        log.info("Google User Info Response: {}", response.getBody());
        return parseResponse(response.getBody(), GoogleUserInfoResponseDto.class);
    }

    private <T> T parseResponse(String response, Class<T> valueType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(response, valueType);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 실패. Response: {}", response, e);
            throw new RuntimeException("JSON 파싱 실패", e);
        }
    }

    private LoginResponse processGoogleLogin(GoogleUserInfoResponseDto userInfo, GoogleLoginDto.TokenResponse tokenInfo) {
        Member member = memberRepository.findBySocialIdAndLoginType(userInfo.getId(), "GOOGLE")
                .orElseGet(() -> {
                    Member newMember = Member.createMember(null, userInfo.getName(), "GOOGLE", userInfo.getId());
                    return memberRepository.save(newMember);
                });

        JwtTokens jwtTokens = jwtTokensGenerator.generate(member.getId().toString());
        log.info("Generated JWT tokens for user {}: Access={}, Refresh={}",
                member.getId(),
                jwtTokens.getAccessToken(),
                jwtTokens.getRefreshToken()
        );

        // Redis에 JWT 리프레시 토큰 저장
        refreshTokenRedisRepository.save(
                "JWT_REFRESH_" + member.getId().toString(),
                jwtTokens.getRefreshToken(),
                TOKEN_EXPIRATION_TIME_MS
        );

        // 구글 액세스 토큰은 항상 저장
        refreshTokenRedisRepository.save(
                "GOOGLE_ACCESS_" + member.getId().toString(),
                tokenInfo.getAccess_token(),
                TOKEN_EXPIRATION_TIME_MS
        );

        // 리프레시 토큰이 있는 경우에만 저장 (최초 로그인 시)
        if (tokenInfo.getRefresh_token() != null) {
            refreshTokenRedisRepository.save(
                    "GOOGLE_REFRESH_" + member.getId().toString(),
                    tokenInfo.getRefresh_token(),
                    TOKEN_EXPIRATION_TIME_MS
            );
        }

        return new LoginResponse(jwtTokens);
    }
}
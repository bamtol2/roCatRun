package com.ssafy.roCatRun.domain.member.service.auth;

import com.ssafy.roCatRun.domain.member.dto.response.LoginResponse;
import com.ssafy.roCatRun.domain.member.repository.MemberRepository;
import com.ssafy.roCatRun.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Slf4j  // logger 선언을 대체
@Service
@RequiredArgsConstructor
@Transactional
public class KakaoService {

    private final MemberRepository memberRepository;  // UserRepository -> MemberRepository로 변경
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${oauth2.kakao.client-id}")
    private String clientId;

    @Value("${oauth2.kakao.redirect-uri}")
    private String redirectUri;
}

public LoginResponse kakaoLogin(String code, String currentDomain){
    //0. 동적으로 redirect URI 선택
    String redirectUri=selectRedirectUri(currentDomain);

    // 1. "인가 코드"로 "액세스 토큰" 요청
    String accessToken = getAccessToken(code, redirectUri);

    // 2. 토큰으로 카카오 API 호출
    HashMap<String, Object> userInfo= getKakaoUserInfo(accessToken);

    //3. 카카오ID로 회원가입 & 로그인 처리
    LoginResponse kakaoUserResponse= kakaoUserLogin(userInfo);

    return kakaoUserResponse;
}


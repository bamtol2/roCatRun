//package com.ssafy.roCatRun.domain.member.service.auth;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ssafy.roCatRun.domain.member.dto.response.LoginResponse;
//import com.ssafy.roCatRun.domain.member.dto.token.AuthTokens;
//import com.ssafy.roCatRun.domain.member.repository.MemberRepository;
//import com.ssafy.roCatRun.global.security.jwt.JwtTokenProvider;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class NaverService {
//
//    private final MemberRepository memberRepository;
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Value("${oauth2.naver.client-id}")
//    private String clientId;
//
//    @Value("${oauth2.naver.client-secret}")
//    private String clientSecret;
//
//    @Value("${oauth2.naver.redirect-uri}")
//    private String redirectUri;
//
//    public LoginResponse naverLogin(String code, String state, String currentDomain) {
//        //0. 동적으로 redirect URI 선택
//        String redirectUri = selectRedirectUri(currentDomain);
//
//        // 1. "인가 코드"로 "액세스 토큰" 요청
//        String accessToken = getAccessToken(code, state, redirectUri);
//
//        // 2. 토큰으로 네이버 API 호출
//        HashMap<String, Object> userInfo = getNaverUserInfo(accessToken);
//
//        //3. 네이버ID로 회원가입 & 로그인 처리
//        LoginResponse naverUserResponse = naverUserLogin(userInfo);
//
//        return naverUserResponse;
//    }
//
//    private String getAccessToken(String code, String state, String redirectUri) {
//        // HTTP Header 생성
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        // HTTP Body 생성
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "authorization_code");
//        body.add("client_id", clientId);
//        body.add("client_secret", clientSecret);
//        body.add("redirect_uri", redirectUri);
//        body.add("code", code);
//        body.add("state", state);
//
//        // HTTP 요청 보내기
//        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(body, headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://nid.naver.com/oauth2.0/token",
//                HttpMethod.POST,
//                naverTokenRequest,
//                String.class
//        );
//
//        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
//        String responseBody = response.getBody();
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = null;
//        try {
//            jsonNode = objectMapper.readTree(responseBody);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return jsonNode.get("access_token").asText();
//    }
//
//    private HashMap<String, Object> getNaverUserInfo(String accessToken) {
//        HashMap<String, Object> userInfo = new HashMap<>();
//
//        // HTTP Header 생성
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + accessToken);
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        // HTTP 요청 보내기
//        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://openapi.naver.com/v1/nid/me",
//                HttpMethod.POST,
//                naverUserInfoRequest,
//                String.class
//        );
//
//        // responseBody에 있는 정보를 꺼냄
//        String responseBody = response.getBody();
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = null;
//        try {
//            jsonNode = objectMapper.readTree(responseBody);
//            jsonNode = jsonNode.get("response"); // 네이버는 response 객체 안에 사용자 정보가 있음
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        String id = jsonNode.get("id").asText();
//        String email = jsonNode.get("email").asText();
//        String nickname = jsonNode.get("nickname").asText();
//
//        userInfo.put("id", id);
//        userInfo.put("email", email);
//        userInfo.put("nickname", nickname);
//
//        return userInfo;
//    }
//
//    private LoginResponse naverUserLogin(HashMap<String, Object> userInfo) {
//        String uid = userInfo.get("id").toString();
//        String naverEmail = userInfo.get("email").toString();
//        String nickName = userInfo.get("nickname").toString();
//
//        User naverUser = memberRepository.findByEmail(naverEmail).orElse(null);
//
//        if (naverUser == null) {    //회원가입
//            naverUser = new User();
//            naverUser.setUid(uid);
//            naverUser.setNickname(nickName);
//            naverUser.setEmail(naverEmail);
//            naverUser.setLoginType("naver");
//            userRepository.save(naverUser);
//        }
//        //토큰 생성
//        AuthTokens token = authTokensGenerator.generate(uid);
//        return new LoginResponse(uid, nickName, naverEmail, token);
//    }
//}
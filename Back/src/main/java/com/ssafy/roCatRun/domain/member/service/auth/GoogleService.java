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
//public class GoogleService {
//
//    private final MemberRepository memberRepository;
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Value("${oauth2.google.client-id}")
//    private String clientId;
//
//    @Value("${oauth2.google.client-secret}")
//    private String clientSecret;
//
//    @Value("${oauth2.google.redirect-uri}")
//    private String redirectUri;
//
//    public LoginResponse googleLogin(String code, String currentDomain) {
//        //0. 동적으로 redirect URI 선택
//        String redirectUri = selectRedirectUri(currentDomain);
//
//        // 1. "인가 코드"로 "액세스 토큰" 요청
//        String accessToken = getAccessToken(code, redirectUri);
//
//        // 2. 토큰으로 구글 API 호출
//        HashMap<String, Object> userInfo = getGoogleUserInfo(accessToken);
//
//        //3. 구글ID로 회원가입 & 로그인 처리
//        LoginResponse googleUserResponse = googleUserLogin(userInfo);
//
//        return googleUserResponse;
//    }
//
//    private String getAccessToken(String code, String redirectUri) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "authorization_code");
//        body.add("client_id", clientId);
//        body.add("client_secret", clientSecret);
//        body.add("redirect_uri", redirectUri);
//        body.add("code", code);
//
//        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity<>(body, headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://oauth2.googleapis.com/token",
//                HttpMethod.POST,
//                googleTokenRequest,
//                String.class
//        );
//
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
//    private HashMap<String, Object> getGoogleUserInfo(String accessToken) {
//        HashMap<String, Object> userInfo = new HashMap<>();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + accessToken);
//
//        HttpEntity<MultiValueMap<String, String>> googleUserInfoRequest = new HttpEntity<>(headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://www.googleapis.com/oauth2/v2/userinfo",
//                HttpMethod.GET,  // Google은 GET 메서드 사용
//                googleUserInfoRequest,
//                String.class
//        );
//
//        String responseBody = response.getBody();
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = null;
//        try {
//            jsonNode = objectMapper.readTree(responseBody);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        String id = jsonNode.get("id").asText();
//        String email = jsonNode.get("email").asText();
//        String name = jsonNode.get("name").asText();
//
//        userInfo.put("id", id);
//        userInfo.put("email", email);
//        userInfo.put("nickname", name);  // Google은 name을 nickname으로 사용
//
//        return userInfo;
//    }
//
//    private LoginResponse googleUserLogin(HashMap<String, Object> userInfo) {
//        String uid = userInfo.get("id").toString();
//        String googleEmail = userInfo.get("email").toString();
//        String nickName = userInfo.get("nickname").toString();
//
//        User googleUser = memberRepository.findByEmail(googleEmail).orElse(null);
//
//        if (googleUser == null) {    //회원가입
//            googleUser = new User();
//            googleUser.setUid(uid);
//            googleUser.setNickname(nickName);
//            googleUser.setEmail(googleEmail);
//            googleUser.setLoginType("google");
//            userRepository.save(googleUser);
//        }
//        //토큰 생성
//        AuthTokens token = authTokensGenerator.generate(uid);
//        return new LoginResponse(uid, nickName, googleEmail, token);
//    }
//}
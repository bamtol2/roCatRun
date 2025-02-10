package com.ssafy.roCatRun.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration  // 스프링의 설정 클래스임을 표시
public class WebConfig implements WebMvcConfigurer {

    // CORS 설정을 추가하는 메서드
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 경로에 대해
                // 이 주소들에서의 접근 허용
                .allowedOrigins(
                        "http://localhost:8081",
                        "http://i12e205.p.ssafy.io:8081"
                )
                // 허용할 HTTP 메서드들
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // 모든 종류의 헤더 허용
                .allowedHeaders("*")
                // 쿠키 사용 허용
                .allowCredentials(true)
                // CORS 예비 요청 결과를 1시간동안 캐시
                .maxAge(3600);
    }
}
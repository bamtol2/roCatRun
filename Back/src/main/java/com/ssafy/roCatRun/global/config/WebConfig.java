package com.ssafy.roCatRun.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 허용할 Origin 목록
                .allowedOrigins(
                        "http://localhost:8080",
                        "http://i12e205.p.ssafy.io:8080"
                )
                // 허용할 HTTP 메서드
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // 허용할 헤더
                .allowedHeaders("*")
                // Credentials 허용
                .allowCredentials(true)
                // preflight 요청의 캐시 시간
                .maxAge(3600);
    }
}
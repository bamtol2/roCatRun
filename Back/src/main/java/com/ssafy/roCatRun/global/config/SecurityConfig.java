package com.ssafy.roCatRun.global.config;

import com.ssafy.roCatRun.global.security.jwt.JwtTokenProvider;
import com.ssafy.roCatRun.global.security.jwt.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 세션 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/domain/members/**").permitAll()
                        .requestMatchers("/domain/mypage/**").permitAll()
                        .requestMatchers("/domain/characters/**").permitAll()
                        .requestMatchers("/domain/upload/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // 관리자 권한 필요
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        // 인증 필요
                        .requestMatchers("/domain/inventory/**").authenticated()
                        .requestMatchers("/domain/items/draw").authenticated()
                        .requestMatchers("/domain/items/**").authenticated()
                        .anyRequest().authenticated()
                )

                // JWT 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // URL 특수문자 허용 설정
    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 오리진 목록
        String[] allowedOrigins = {
                "http://i12e205.p.ssafy.io:8080",
                "http://localhost:8080",
                "https://i12e205.p.ssafy.io:8080",
                "https://localhost:8080",
                "http://localhost:9092",
                "https://localhost:9092",
                "http://i12e205.p.ssafy.io:9092",
                "https://i12e205.p.ssafy.io:9092",
                "http://localhost:8081",
                "http://i12e205.p.ssafy.io:8081"
        };
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));

        // HTTP 메서드 설정
        String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"};
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));

        // 헤더 설정
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("Authorization");

        // 인증 설정
        configuration.setAllowCredentials(true);

        // pre-flight 요청 캐시 시간
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
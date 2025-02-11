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
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration  // 스프링의 설정 클래스임을 표시
@EnableWebSecurity  // 스프링 시큐리티 기능을 활성화
@RequiredArgsConstructor  // final 필드의 생성자를 자동으로 만들어줌
public class SecurityConfig {

    // JWT 토큰 관련 기능을 가진 클래스를 가져옴
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF: 웹 사이트 간 공격을 막는 기능인데,
                // REST API는 필요없어서 끔
                .csrf(csrf -> csrf.disable())

                // CORS: 다른 도메인(주소)에서의 접근 규칙을 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 세션 설정: JWT를 사용하므로 세션은 사용하지 않음
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // "/api/auth/**" 경로는 누구나 접근 가능
                        .requestMatchers("/api/auth/**").permitAll()
                        // 닉네임 중복 체크도 누구나 가능
                        .requestMatchers("/domain/characters/**").permitAll()
                        // Swagger 관련 경로도 누구나 접근 가능
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 서버 상태 체크는 누구나 가능
                        .requestMatchers("/actuator/health").permitAll()
                        // 서버 관리 기능은 관리자만 가능
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        // 나머지는 로그인한 사용자만 가능
                        .anyRequest().authenticated()
                )
                // JWT 검사하는 필터를 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // URL에  특수문자 허용 설정
    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        // URL에 인코딩된 특수문자 허용
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    // CORS 상세 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 접근 허용할 프론트엔드 주소들
        configuration.addAllowedOrigin("http://i12e205.p.ssafy.io:8080");
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("https://i12e205.p.ssafy.io:8080");
        configuration.addAllowedOrigin("https://localhost:8080");
        configuration.addAllowedOrigin("http://localhost:9092");
        configuration.addAllowedOrigin("https://localhost:9092");
        configuration.addAllowedOrigin("http://i12e205.p.ssafy.io:9092");
        configuration.addAllowedOrigin("https://i12e205.p.ssafy.io:9092");
        // 모든 HTTP 메서드 허용 (GET, POST 등)
        configuration.addAllowedMethod("*");
        // 모든 헤더 허용
        configuration.addAllowedHeader("*");
        // 쿠키 사용 허용
        configuration.setAllowCredentials(true);
        // 브라우저에서 Authorization 헤더 접근 허용
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 위의 설정 적용
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
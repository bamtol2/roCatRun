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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (REST API이므로 불필요)
                .csrf(csrf -> csrf.disable())
                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 세션 관리 설정 (JWT 사용으로 STATELESS로 설정)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // URL별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // 인증 관련 엔드포인트는 모두 허용
                        .requestMatchers("/api/characters/check-nickname/**").permitAll()  // 닉네임 중복 체크는 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Swagger UI 접근 허용
                        .requestMatchers("/actuator/health").permitAll()  // 헬스 체크 허용
                        .requestMatchers("/actuator/**").hasRole("ADMIN")  // 액추에이터는 관리자만
                        .anyRequest().authenticated()  // 그 외 요청은 인증 필요
                )
                // JWT 인증 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 허용할 Origin 설정 (프론트엔드 서버 주소)
        configuration.addAllowedOrigin("http://i12e205.p.ssafy.io:8080");
        configuration.addAllowedOrigin("http://localhost:8080");
        // 허용할 HTTP 메서드 설정
        configuration.addAllowedMethod("*");
        // 허용할 헤더 설정
        configuration.addAllowedHeader("*");
        // Credentials 허용 설정
        configuration.setAllowCredentials(true);
        // Authorization 헤더 노출 설정
        configuration.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
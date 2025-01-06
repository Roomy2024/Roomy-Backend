package com.example.Roomy.SocialLogin.Config;

import com.example.Roomy.SocialLogin.Util.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class config {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public config(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",               // 인증 API
                                "/community/**",          // 커뮤니티 관련 API
                                "/likes/**",              // 좋아요 API
                                "/comments/**",           // 댓글 API
                                "/swagger-ui/**",     // Swagger UI 리소스
                                "/v3/api-docs/**",    // OpenAPI JSON
                                "/swagger-ui.html",   // Swagger HTML
                                "/v3/api-docs.yaml"   // OpenAPI YAML
                        ).permitAll() // 위 경로는 인증 없이 접근 허용
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 기반 인증: 세션 미사용
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 등록
        return http.build();
    }
}

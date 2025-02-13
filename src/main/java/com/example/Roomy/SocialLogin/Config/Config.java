package com.example.Roomy.SocialLogin.Config;

import com.example.Roomy.SocialLogin.Controller.SuccessHandler;
import com.example.Roomy.SocialLogin.JWT.JwtAuthenticationFilter;
import com.example.Roomy.SocialLogin.Service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class Config {

    private final SuccessHandler successHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(basic -> basic.disable()) // HTTP Basic 비활성화
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .cors(Customizer.withDefaults()) // CORS 활성화
                .authorizeHttpRequests(auth -> auth
                        // Swagger 및 API 문서 경로 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/webjars/**").permitAll()
                        // OAuth 관련 경로 허용
                        .requestMatchers("/oauth2/authorization/kakao").permitAll()
                        .requestMatchers("/auth/kakao/**").permitAll()
                        .requestMatchers("/update-userinfo").permitAll()
                        // 기타 특정 경로 허용
                        .requestMatchers("/report/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 모든 API 요청 허용 (개발 환경에서만 사용 권장)
                        .requestMatchers("/community/**").permitAll()
                        .requestMatchers("/comments/**").permitAll()
                        .requestMatchers("/groupchat/**").permitAll()
                        .requestMatchers("/likes/**").permitAll()
                        .requestMatchers("/onetoonechat/**").permitAll()
                        .requestMatchers("/bookmarks/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/notifications/**").permitAll()
                        .requestMatchers("/mypage/**").permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
                .oauth2Login(oauth2 -> {
                    // 사용자 정보 로딩 시 커스텀 서비스 설정
                    oauth2.userInfoEndpoint(userInfoEndpointConfig ->
                            userInfoEndpointConfig.userService(customOAuth2UserService)
                    );
                    // 로그인 성공 시 핸들러 설정
                    oauth2.successHandler(successHandler);
                });

        return http.build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000")); // React 개발 서버
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // 허용 메서드
        config.setAllowedHeaders(List.of("*")); // 허용 헤더
        config.setExposedHeaders(List.of("*")); // 노출 헤더

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

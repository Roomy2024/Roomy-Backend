package com.example.Roomy.SocialLogin.Config;

import com.example.Roomy.SocialLogin.Util.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class config {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public config(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/likes/**", "/community/**", "/comments/**", "/onetoonechat/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().permitAll() // 그 외 요청 허용
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터 등록
        return http.build();
    }

    //CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //쿠키, 인증정보 포함 허용
        corsConfiguration.setAllowCredentials(true);
        //허용할 프론트 주소 설정
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
        //허용할 GTTP 메서드 설정
        corsConfiguration.setAllowedMethods(List.of("GET","POST","DELETE","OPTIONS"));
        //허용할 요청 헤더 설정
        corsConfiguration.setAllowedHeaders(List.of("*"));

        //CORS 설정을 URL 패턴에 등록
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);
        return source;
    }
}
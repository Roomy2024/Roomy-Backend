package com.example.Roomy.SocialLogin.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        // 개발 서버 설정
        Server devServer = new Server();
        devServer.setUrl("/"); // 개발 환경의 기본 URL
        devServer.setDescription("개발 서버");

        // 운영 서버 설정
        Server prodServer = new Server();
        prodServer.setUrl("http://localhost:8000"); // 운영 환경의 URL
        prodServer.setDescription("운영 서버");

        // API 정보 설정
        Info info = new Info()
                .title("Swagger API")        // API 문서 제목
                .version("v1.0.0")           // API 문서 버전
                .description("스웨거 API");   // API 문서 설명

        // OpenAPI 객체 반환
        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}

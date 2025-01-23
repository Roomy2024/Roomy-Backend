package com.example.Roomy.SocialLogin.Model;

import com.example.Roomy.SocialLogin.UserRole;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@Builder
@ToString
public class OAuth2UserInfo {
    private String id;
    private String email;
    private String provider;

    public static OAuth2UserInfo of(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return ofGoogle(attributes);
            case "kakao":
                return ofKakao(attributes);
            default:
                throw new RuntimeException();
        }
    }
    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .provider("google")
                .id("google_" + (String) attributes.get("sub"))
                .email((String) attributes.get("email"))
                .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .provider("kakao")
                .id("kakao_" + attributes.get("id").toString())
                .email((String) ((Map) attributes.get("kakao_account")).get("email"))
                .build();
    }

    // User 엔티티로 변환
    public User toEntity() {
        return User.builder()
                .socailId(id)
                .provider(provider)
                .email(email)
                .role(UserRole.MEMBER)  // 기본 역할 설정 (필요에 따라 수정 가능)
                .build();
    }
}

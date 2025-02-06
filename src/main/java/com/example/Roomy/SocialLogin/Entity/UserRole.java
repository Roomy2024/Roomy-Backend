package com.example.Roomy.SocialLogin.Entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    MEMBER("사용자"),
    ADMIN("관리자");

    private final String displayValue;
}

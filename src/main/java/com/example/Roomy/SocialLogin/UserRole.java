package com.example.Roomy.SocialLogin;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    MEMBER("사용자");

    private final String displayValue;
}

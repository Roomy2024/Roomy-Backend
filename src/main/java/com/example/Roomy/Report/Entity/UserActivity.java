package com.example.Roomy.Report.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserActivity {
    ACTIVITY("일반 유저"),
    BAN("정지 유저");

    private final String displayValue;
}

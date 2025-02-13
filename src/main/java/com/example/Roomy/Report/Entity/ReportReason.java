package com.example.Roomy.Report.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ReportReason {
    ILLEGAL_INFORMATION("불법정보"),
    ABUSE("욕설, 인신공격"),
    OBSCENE_CONTENT("음란성, 선정성"),
    COMMERCIAL_PROMOTION("영리목적, 홍보성"),
    PERSONAL_INFO_LEAK("개인정보 노출"),
    SPAM("도배"),
    MALICIOUS_CODE("악성코드"),
    HATE_SPEECH("혐오 발언, 암시"),
    VIOLENCE_ORGANIZATION("폭력, 위험한 조직"),
    FALSE_INFORMATION("거짓 정보");

    private final String displayValue;

    // 한글로 Enum 변환 (JSON에서 한글을 Enum으로 변환할 때 사용)
    public static ReportReason fromDisplayValue(String displayValue) {
        return Arrays.stream(ReportReason.values())
                .filter(reason -> reason.getDisplayValue().equals(displayValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고 사유입니다: " + displayValue));
    }
}

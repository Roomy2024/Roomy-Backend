package com.example.Roomy.Report.Entity;

import jakarta.persistence.AttributeConverter;

public class ReportReasonConverter implements AttributeConverter<ReportReason, String> {

    @Override
    public String convertToDatabaseColumn(ReportReason reportReason) {
        if (reportReason == null) {
            return null;
        }
        return reportReason.getDisplayValue(); // 한글 값 저장
    }

    @Override
    public ReportReason convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return ReportReason.fromDisplayValue(dbData); // DB에서 불러올 때 Enum 변환
    }
}

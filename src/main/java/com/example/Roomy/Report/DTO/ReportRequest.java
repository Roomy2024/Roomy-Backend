package com.example.Roomy.Report.DTO;

import com.example.Roomy.Report.Entity.ReportReason;
import com.example.Roomy.SocialLogin.Entity.User;
import lombok.Data;

@Data
public class ReportRequest {
    private String reportReason;

    public ReportReason toEnum() {
        return ReportReason.fromDisplayValue(reportReason); // 한글을 Enum으로 변환
    }
}

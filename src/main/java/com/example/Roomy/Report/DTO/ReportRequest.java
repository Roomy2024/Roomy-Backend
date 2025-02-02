package com.example.Roomy.Report.DTO;

import com.example.Roomy.SocialLogin.Entity.User;
import lombok.Data;

@Data
public class ReportRequest {
    private String type;
    private Long repoterId;
    private Long reportedId;
}

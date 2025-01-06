package com.example.Roomy.report.service;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.community.entity.CommunityEntity;
import com.example.Roomy.report.entity.ReportEntity;
import com.example.Roomy.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public void reportCommunity(User reporter, CommunityEntity reportedCommunity, String reason){
        ReportEntity reportEntity = ReportEntity.builder()
                .reporter(reporter)
                .reportedCommunity(reportedCommunity)
                .reason(reason)
                .build();
        reportRepository.save(reportEntity);
    }
}

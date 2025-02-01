package com.example.Roomy.Report.Service;

import com.example.Roomy.Report.Entity.Report;
import com.example.Roomy.Report.Entity.UserActivity;
import com.example.Roomy.Report.Repository.ReportRepository;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Transactional
    public Report saveReport(String type, User repoter,  User reported){
        if(reportRepository.existsByReporterAndReported(reported, reported))
        {
            throw new RuntimeException("이미 신고한 이력이 있습니다.");
        }

        Report report = new Report();

        switch (type){
            case "chatting":
                report.setType("chatting");
                break;
            case "community":
                report.setType("community");
                break;
            case "comment":
                report.setType("comment");
                break;
        }
        report.setReporter(repoter);
        report.setReported(reported);
        report.setLocalDateTime(LocalDateTime.now());

        reportRepository.save(report);

        reported.setReportCount(reported.getReportCount()+1);

        userRepository.save(reported);

        //신고누적이 10개 이상이 되면 일시정지
        if(reported.getReportCount() >= 10){
            reported.setStatus(UserActivity.BAN);
            userRepository.save(reported);
        }

        return report;
    }
}

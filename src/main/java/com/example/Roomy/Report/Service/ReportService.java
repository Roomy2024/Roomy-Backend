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
    public Report saveReport(String type, Long reporter,  Long reported){
        // ✅ ID를 이용해 실제 User 객체 조회
        User reporterId = userRepository.findById(reporter)
                .orElseThrow(() -> new RuntimeException("신고자를 찾을 수 없습니다."));
        User reportedId = userRepository.findById(reported)
                .orElseThrow(() -> new RuntimeException("신고당한 사용자를 찾을 수 없습니다."));

        //중복 신고 확인
        if (reportRepository.existsByReporterAndReported(reporterId, reportedId)) {
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
        report.setReporter(reporterId);
        report.setReported(reportedId);
        report.setLocalDateTime(LocalDateTime.now());

        reportedId.setReportCount(reportedId.getReportCount()+1);

        reportRepository.save(report);
        userRepository.save(reportedId);

        //신고누적이 10개 이상이 되면 일시정지
        if(reportedId.getReportCount() >= 10){
            reportedId.setStatus(UserActivity.BAN);
            userRepository.save(reportedId);
        }

        return report;
    }
}

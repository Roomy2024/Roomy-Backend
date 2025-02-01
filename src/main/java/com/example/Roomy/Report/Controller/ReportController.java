package com.example.Roomy.Report.Controller;

import com.example.Roomy.Report.DTO.ReportRequest;
import com.example.Roomy.Report.Entity.Report;
import com.example.Roomy.Report.Repository.ReportRepository;
import com.example.Roomy.Report.Service.ReportService;
import com.example.Roomy.SocialLogin.DTO.UserRequest;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.UserRepository;
import com.google.firebase.database.core.Repo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportRepository reportRepository;
    private final ReportService reportService;
    private final UserRepository userRepository;

    @PostMapping("/{type}")
    public ResponseEntity<String> report(@RequestBody ReportRequest reportRequest, @PathVariable String type) {
        //User ID를 사용하여 실제 User 객체 조회
        User reporter = userRepository.findById(reportRequest.getRepoterId())
                .orElseThrow(() -> new RuntimeException("신고자를 찾을 수 없습니다."));
        User reported = userRepository.findById(reportRequest.getReportedId())
                .orElseThrow(() -> new RuntimeException("신고 대상 사용자를 찾을 수 없습니다."));

        //Report 저장
        reportService.saveReport(type, reporter, reported);

        return ResponseEntity.ok("신고가 정상적으로 접수되었습니다.");
    }
}

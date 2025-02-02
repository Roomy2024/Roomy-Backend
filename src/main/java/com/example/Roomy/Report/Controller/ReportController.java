package com.example.Roomy.Report.Controller;

import com.example.Roomy.Report.DTO.ReportRequest;
import com.example.Roomy.Report.Entity.Report;
import com.example.Roomy.Report.Repository.ReportRepository;
import com.example.Roomy.Report.Service.ReportService;
import com.example.Roomy.SocialLogin.DTO.UserRequest;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import com.example.Roomy.SocialLogin.UserRepository;
import com.google.firebase.database.core.Repo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportRepository reportRepository;
    private final ReportService reportService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/{type}")
    public ResponseEntity<String> report(@PathVariable String type, @RequestBody Map<String, Long> requestBody, @RequestHeader("Authorization") String token) {
        //JWT 토큰에서 신고한 사용자 ID 추출
        String jwtToken = token.replace("Bearer ", ""); // "Bearer " 제거
        Long reporter = Long.parseLong(jwtTokenProvider.getUserIdFromToken(jwtToken));

        // ✅ 신고당한 사용자 ID 가져오기 (Body에서 직접 받음)
        Long reported = requestBody.get("reportedId");

        //Report 저장
        reportService.saveReport(type, reporter, reported);

        return ResponseEntity.ok("신고가 정상적으로 접수되었습니다.");
    }
}

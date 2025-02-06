package com.example.Roomy.Report.Service;

import com.example.Roomy.Report.Entity.Report;
import com.example.Roomy.Report.Entity.UserActivity;
import com.example.Roomy.Report.Repository.ReportRepository;
import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.comment.repository.CommentRepository;
import com.example.Roomy.community.repository.CommunityRepository;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Report saveReport(String type, Long reporterId,  Long targetId) {
        // ID를 이용해 실제 User 객체 조회
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("신고자를 찾을 수 없습니다."));

        Report report = new Report();
        User reported = null;

        switch (type) {
            case "community":
                reported = handleCommunityReport(report,targetId);
                break;

            case "comments":
                reported = handleCommentReport(report,targetId);
                break;

            default:
                throw new IllegalArgumentException("유효하지 않은 신고 유형입니다: " + type);
        }

        // 신고 대상이 없는 경우
        if (reported == null) {
            throw new RuntimeException("신고 대상 사용자를 설정할 수 없습니다.");
        }
        //중복 신고 확인
        if (reportRepository.existsByReporterAndReported(reporter, reported)) {
            throw new RuntimeException("이미 신고한 이력이 있습니다.");
        }

        return createReport(type, reporter, reported, report);
    }

    // 신고 생성
    private Report createReport(String type, User reporter, User reported, Report report) {
        report.setType(type);
        report.setReporter(reporter);
        report.setReported(reported);
        report.setLocalDateTime(LocalDateTime.now());
        handleReportedUser(reported);
        return reportRepository.save(report);
    }

    // 신고받은 사용자 처리
    private void handleReportedUser(User reported) {
        // 신고 횟수 증가
        reported.setReportCount(reported.getReportCount() + 1);
        userRepository.save(reported);

        // 신고 누적이 10개 이상이면 계정 정지
        if (reported.getReportCount() >= 10) {
            reported.setStatus(UserActivity.BAN);
            userRepository.save(reported);
        }
    }

    //게시판 신고당한 유저id 추출
    private User handleCommunityReport(Report report, Long communityId) {
        var community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        User reported = community.getAuthor();
        report.setType("community");
        report.setCommunity(community);
        report.setComment(null);  // 댓글 신고가 아니므로 null 설정

        return reported;
    }

    //댓글 신고당한 유저id 추출
    private User handleCommentReport(Report report, Long commentId) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        User reported = comment.getAuthor();
        report.setType("comments");
        report.setComment(comment);
        report.setCommunity(null);  // 게시글 신고가 아니므로 null 설정

        return reported;
    }
}

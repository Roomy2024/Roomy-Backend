package com.example.Roomy.Report.Controller;

import com.example.Roomy.Report.DTO.ReportRequest;
import com.example.Roomy.Report.Entity.ReportReason;
import com.example.Roomy.Report.Repository.ReportRepository;
import com.example.Roomy.Report.Service.ReportService;
import com.example.Roomy.SocialLogin.JWT.JwtTokenProvider;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.comment.repository.CommentRepository;
import com.example.Roomy.community.dto.CommunityDTO;
import com.example.Roomy.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;


    @PostMapping("/{type}/{id}")
    public ResponseEntity<String> report(
            @PathVariable String type,
            @RequestHeader("id") Long targetId,
            @RequestHeader("Authorization") String token,
            @RequestBody ReportRequest reportRequest) {

        //JWT 토큰에서 신고한 사용자 ID 추출
        String jwtToken = token.replace("Bearer ", ""); // "Bearer " 제거
        Long reporterId = Long.parseLong(jwtTokenProvider.getUserIdFromToken(jwtToken));

        // ReportReason Enum 값 가져오기
        ReportReason reportReason = reportRequest.toEnum();

        //Report 저장
        reportService.saveReport(type, reporterId, targetId, reportReason);

        return ResponseEntity.ok("신고가 정상적으로 접수되었습니다.");
    }

    //신고당한 게시글 정보
    @GetMapping("/get_community")
    public ResponseEntity<?> getCommunity(@RequestHeader("id") Long communityId){
        var community = communityRepository.findById(communityId).orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        CommunityDTO response = new CommunityDTO(
                community.getCommunityId(),
                community.getTitle(),
                community.getContent(),
                community.getFileGroupEntity(),
                community.getAuthor().getId()
        );
        return ResponseEntity.ok(response);
    }

    //신고당한 댓글이 위치한 게시글 정보
    @GetMapping("/get_comment_where")
    public ResponseEntity<?> getCommentWhere(@RequestHeader("id") Long commentsId){
        // 댓글 조회
        var comment = commentRepository.findById(commentsId).orElseThrow(() -> new RuntimeException("해당 댓글을 찾을 수 없습니다."));

        // 댓글이 속한 게시글 조회
        var community = comment.getCommunity();
        if (community == null) {
            return ResponseEntity.badRequest().body("해당 댓글이 속한 게시글을 찾을 수 없습니다.");
        }

        // DTO 변환 후 반환 (무한루프 방지)
        CommunityDTO response = new CommunityDTO(
                community.getCommunityId(),
                community.getTitle(),
                community.getContent(),
                community.getFileGroupEntity(),
                community.getAuthor().getId()
        );

        return ResponseEntity.ok(response);
    }
}

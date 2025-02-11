package com.example.Roomy.community.controller;

import com.example.Roomy.community.dto.CommunityRequestDTO;
import com.example.Roomy.community.dto.CommunityResponseDTO;
import com.example.Roomy.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "커뮤니티 게시글 생성", description = "새로운 커뮤니티 게시글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/create")
    public ResponseEntity<CommunityResponseDTO> createCommunity(@ModelAttribute CommunityRequestDTO communityRequestDTO) throws IOException {
        return ResponseEntity.ok(communityService.createCommunity(communityRequestDTO));
    }

    @Operation(summary = "커뮤니티 게시글 수정", description = "기존 커뮤니티 게시글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/update/{id}")
    public ResponseEntity<CommunityResponseDTO> updateCommunity(
            @Parameter(description = "게시글 ID") @PathVariable Long id,
            @ModelAttribute CommunityRequestDTO communityRequestDTO
    ) throws IOException {
        return ResponseEntity.ok(communityService.updateCommunity(id, communityRequestDTO));
    }

    @Operation(summary = "커뮤니티 게시글 조회", description = "특정 커뮤니티 게시글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })

    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponseDTO> getCommunity(
            @Parameter(description = "게시글 ID") @PathVariable Long id) {
        communityService.increaseViewCount(id);
        return ResponseEntity.ok(communityService.getCommunity(id));
    }

    @Operation(summary = "전체 커뮤니티 게시글 조회 (페이징)", description = "등록된 모든 커뮤니티 게시글을 10개씩 페이지로 조회합니다.")
    @GetMapping("/getall")
    public ResponseEntity<Page<CommunityResponseDTO>> getAllCommunities(
            @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10); // ✅ 10개씩 페이징
        return ResponseEntity.ok(communityService.getAllCommunities(pageable));
    }


    @Operation(summary = "사용자의 커뮤니티 게시글 조회", description = "특정 사용자가 작성한 커뮤니티 게시글 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자의 게시글이 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/my/{userId}")
    public ResponseEntity<List<CommunityResponseDTO>> getMyCommunities(
            @Parameter(description = "유저 ID") @PathVariable Long userId) {
        return ResponseEntity.ok(communityService.getMyCommunities(userId));
    }

    @Operation(summary = "커뮤니티 게시글 삭제", description = "특정 사용자가 커뮤니티 게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (게시글 작성자가 아님)"),
            @ApiResponse(responseCode = "404", description = "게시글이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCommunity(
            @Parameter(description = "게시글 ID") @PathVariable Long id,
            @Parameter(description = "유저 ID") @RequestParam Long userId) {
        return ResponseEntity.ok(communityService.deleteCommunity(id, userId));
    }
}

package com.example.Roomy.like.controller;


import com.example.Roomy.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{communityId}/like")
    public ResponseEntity<Void> like(@PathVariable Long communityId, @RequestParam Long userId) {
        likeService.like(communityId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{communityId}/count")
    public ResponseEntity<Long> countLikes(@PathVariable Long communityId) {
        long likeCount = likeService.countLikes(communityId);
        return ResponseEntity.ok(likeCount);
    }
}
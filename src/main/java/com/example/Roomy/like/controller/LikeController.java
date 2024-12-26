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

    @PostMapping("/{communityId}/unlike")
    public ResponseEntity<Void> unlike(@PathVariable Long communityId, @RequestParam Long userId) {
        likeService.unlike(communityId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{communityId}/count")
    public ResponseEntity<Long> countLikes(@PathVariable Long communityId) {
        long likeCount = likeService.countLikes(communityId);
        return ResponseEntity.ok(likeCount);
    }

    @GetMapping("/{communityId}/is-liked")
    public ResponseEntity<Boolean> isLikedByUser(@PathVariable Long communityId, @RequestParam Long userId){
        boolean isLiked = likeService.isLikedByUser(communityId, userId);
        return ResponseEntity.ok(isLiked);
    }
}

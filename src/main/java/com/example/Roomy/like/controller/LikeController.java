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

    @PostMapping("/{type}/{id}/like-toggle")
    public ResponseEntity<String> toggleLike(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestParam Long userId) {
        likeService.toggleLike(id, userId, type);
        return ResponseEntity.ok("Like toggled successfully.");
    }

    @GetMapping("/{type}/{id}/count")
    public ResponseEntity<Long> countLikes(
            @PathVariable String type,
            @PathVariable Long id) {
        long count = likeService.countLikes(id, type);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{type}/{id}/is-liked")
    public ResponseEntity<Boolean> isLikedByUser(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestParam Long userId) {
        boolean isLiked = likeService.isLikedByUser(id, userId, type);
        return ResponseEntity.ok(isLiked);
    }
}

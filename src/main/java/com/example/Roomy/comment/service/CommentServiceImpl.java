package com.example.Roomy.comment.service;

import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.SocialLogin.Repository.UserRepository;
import com.example.Roomy.comment.dto.*;
import com.example.Roomy.comment.entity.CommentEntity;
import com.example.Roomy.comment.entity.ReplyEntity;
import com.example.Roomy.comment.repository.CommentRepository;
import com.example.Roomy.comment.repository.ReplyRepository;
import com.example.Roomy.community.entity.CommunityEntity;
import com.example.Roomy.community.repository.CommunityRepository;
import com.example.Roomy.notification.repository.NotificationRepository;
import com.example.Roomy.notification.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public CommentResponseDTO addComment(CommentRequestDTO commentRequestDTO) {
        CommunityEntity community = communityRepository.findById(commentRequestDTO.getCommunityId())
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));
        User user = userRepository.findById(commentRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CommentEntity comment = CommentEntity.builder()
                .community(community)
                .author(user)
                .content(commentRequestDTO.getContent())
                .build();
        CommentEntity savedComment = commentRepository.save(comment);

        // 본인이 자신의 커뮤니티에 댓글을 달았을 경우 알림을 보내지 않음
        if (!user.getId().equals(community.getAuthor().getId())) {
            notificationService.sendNotification( // ✅ 인스턴스 메서드 호출
                    user.getId(),
                    community.getAuthor().getId(),
                    "[댓글 알림] " + community.getTitle() + " 에 " + user.getUsername() + "님이 댓글을 남겼습니다: " + commentRequestDTO.getContent()
            );
        }

        return toCommentResponseDTO(savedComment);
    }

    @Override
    @Transactional
    public ReplyResponseDTO addReply(ReplyRequestDTO replyRequestDTO) {
        CommentEntity parentComment = commentRepository.findById(replyRequestDTO.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        User user = userRepository.findById(replyRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ReplyEntity reply = ReplyEntity.builder()
                .parentComment(parentComment)
                .author(user)
                .content(replyRequestDTO.getContent())
                .build();
        ReplyEntity savedReply = replyRepository.save(reply);

        return toReplyResponseDTO(savedReply);
    }

    @Override
    public List<CommentResponseDTO> getCommentsByCommunityId(Long communityId) {
        return commentRepository.findByCommunity_CommunityId(communityId).stream()
                .map(this::toCommentResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReplyResponseDTO> getRepliesByCommentId(Long commentId) {
        return replyRepository.findByParentComment_CommentId(commentId).stream()
                .map(this::toReplyResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new IllegalStateException("Only the author can delete this comment");
        }
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void deleteReply(Long replyId, Long userId) {
        ReplyEntity reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("Reply not found"));
        if (!reply.getAuthor().getId().equals(userId)) {
            throw new IllegalStateException("Only the author can delete this reply");
        }
        replyRepository.delete(reply);
    }

    @Override
    @Transactional
    public void updateComment(Long commentId, UpdateRequestDTO updateRequestDTO) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 작성자 검증
        if (!comment.getAuthor().getId().equals(updateRequestDTO.getUserId())) {
            throw new IllegalStateException("댓글 작성자만 수정할 수 있습니다.");
        }

        // 댓글 내용 업데이트
        comment.setContent(updateRequestDTO.getContent());
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void updateReply(Long replyId, UpdateRequestDTO updateRequestDTO) {
        ReplyEntity reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("대댓글을 찾을 수 없습니다."));

        // 작성자 검증
        if (!reply.getAuthor().getId().equals(updateRequestDTO.getUserId())) {
            throw new IllegalStateException("대댓글 작성자만 수정할 수 있습니다.");
        }

        // 대댓글 내용 업데이트
        reply.setContent(updateRequestDTO.getContent());
        replyRepository.save(reply);
    }

    private CommentResponseDTO toCommentResponseDTO(CommentEntity comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .communityId(comment.getCommunity().getCommunityId())
                .author(comment.getAuthor().getUsername())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replies(comment.getReplies().stream().map(this::toReplyResponseDTO).collect(Collectors.toList()))
                .build();
    }

    private ReplyResponseDTO toReplyResponseDTO(ReplyEntity reply) {
        return ReplyResponseDTO.builder()
                .replyId(reply.getReplyId())
                .commentId(reply.getParentComment().getCommentId())
                .author(reply.getAuthor().getUsername())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .build();
    }
}

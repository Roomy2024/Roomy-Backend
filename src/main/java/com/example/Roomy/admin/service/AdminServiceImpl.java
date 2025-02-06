package com.example.Roomy.admin.service;

import com.example.Roomy.admin.dto.*;
import com.example.Roomy.chat.service.groupchat.GroupChatService;
import com.example.Roomy.chat.service.privatechat.PrivateChatService;
import com.example.Roomy.community.entity.CommunityEntity;
import com.example.Roomy.comment.entity.CommentEntity;
import com.example.Roomy.comment.entity.ReplyEntity;
import com.example.Roomy.community.repository.CommunityRepository;
import com.example.Roomy.comment.repository.CommentRepository;
import com.example.Roomy.comment.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final GroupChatService groupChatService; // 그룹 채팅 서비스 주입
    private final PrivateChatService privateChatService; // 1:1 채팅 서비스 주입

    @Override
    public List<AdminCommunityDTO> getAllPosts() {
        return communityRepository.findAll()
                .stream()
                .map(post -> new AdminCommunityDTO(post.getCommunityId(), post.getTitle(), post.getAuthor().getEmail(), post.getCreatedAt(), post.getViews()))
                .toList();
    }

    @Override
    public AdminCommunityDTO getPostDetail(Long postId) {
        return communityRepository.findById(postId)
                .map(post -> new AdminCommunityDTO(post.getCommunityId(), post.getTitle(), post.getAuthor().getEmail(), post.getCreatedAt(), post.getViews()))
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    @Transactional
    @Override
    public void deletePost(Long postId) {
        CommunityEntity post = communityRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        post.setTitle("관리자에 의해 삭제된 글입니다.");
        post.setContent("관리자에 의해 삭제된 글입니다.");
        communityRepository.save(post);
    }

    @Override
    public List<AdminCommentDTO> getCommentsByPostId(Long postId) {
        return commentRepository.findByCommunity_CommunityId(postId)
                .stream()
                .map(comment -> new AdminCommentDTO(comment.getCommentId(), comment.getContent(), comment.getAuthor().getEmail()))
                .toList();
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        comment.setContent("관리자에 의해 삭제된 댓글입니다.");
        commentRepository.save(comment);
    }

    @Override
    public List<AdminReplyDTO> getRepliesByCommentId(Long commentId) {
        return replyRepository.findByParentComment_CommentId(commentId)
                .stream()
                .map(reply -> new AdminReplyDTO(reply.getReplyId(), reply.getContent(), reply.getAuthor().getEmail()))
                .toList();
    }

    @Transactional
    @Override
    public void deleteReply(Long replyId) {
        ReplyEntity reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("대댓글을 찾을 수 없습니다."));
        reply.setContent("관리자에 의해 삭제된 대댓글입니다.");
        replyRepository.save(reply);
    }

    @Override
    public List<AdminChatMessageDTO> getGroupChatMessages(String roomId) {
        return groupChatService.getGroupMessages(roomId).join()
                .stream()
                .map(msg -> new AdminChatMessageDTO(msg.getMessageId(), msg.getSender(), msg.getContent()))
                .toList();
    }

    @Override
    public void deleteGroupChatMessage(String roomId, String messageId) {
        groupChatService.deleteGroupMessage(roomId, messageId, "admin");
    }

    @Override
    public List<AdminChatMessageDTO> getPrivateChatMessages(String roomId) {
        return privateChatService.getMessages(roomId).join()
                .stream()
                .map(msg -> new AdminChatMessageDTO(msg.getMessageId(), msg.getSender(), msg.getContent()))
                .toList();
    }

    @Override
    public void deletePrivateChatMessage(String roomId, String messageId) {
        privateChatService.deleteMessage(roomId, messageId, "admin");
    }
}

package com.example.Roomy.admin.service;

import com.example.Roomy.admin.dto.*;

import java.util.List;

public interface AdminService {
    List<AdminCommunityDTO> getAllPosts();
    AdminCommunityDTO getPostDetail(Long postId);
    void deletePost(Long postId);

    List<AdminCommentDTO> getCommentsByPostId(Long postId);
    void deleteComment(Long commentId);

    List<AdminReplyDTO> getRepliesByCommentId(Long commentId);
    void deleteReply(Long replyId);

    List<AdminChatMessageDTO> getGroupChatMessages(String roomId);
    void deleteGroupChatMessage(String roomId, String messageId);

    List<AdminChatMessageDTO> getPrivateChatMessages(String roomId);
    void deletePrivateChatMessage(String roomId, String messageId);
}

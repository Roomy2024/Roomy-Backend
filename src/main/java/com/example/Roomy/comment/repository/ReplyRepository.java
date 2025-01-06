package com.example.Roomy.comment.repository;

import com.example.Roomy.comment.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {
    List<ReplyEntity> findByParentComment_CommentId(Long commentId);
}

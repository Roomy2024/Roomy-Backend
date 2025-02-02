package com.example.Roomy.bookmark.entity;


import com.example.Roomy.SocialLogin.Entity.User;
import com.example.Roomy.community.entity.CommunityEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Bookmark")
public class BookmarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="community_id", nullable = false)
    private CommunityEntity community;

    @Column(nullable = false)
    private boolean active;

}

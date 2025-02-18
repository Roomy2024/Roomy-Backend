package com.example.Roomy.SocialLogin.Entity;


import com.example.Roomy.Report.Entity.Report;
import com.example.Roomy.Report.Entity.UserActivity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String provider;
    private String password;
    private String socailId;

    private String area;
    private String gender;
    //private int age;
    private String profile;

    private String refreshToken;

    //신고 수가 일정 수를 넘으면 임시정지 or 정지
    private int ReportCount;

    @Column(nullable = true,length = 2048)
    private String fcmToken;

    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reporter;  // 사용자가 신고한 기록

    @OneToMany(mappedBy = "reported", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reported;  // 사용자가 신고당한 기록

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserActivity status = UserActivity.ACTIVITY; // 기본값은 ACTIVITY


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}

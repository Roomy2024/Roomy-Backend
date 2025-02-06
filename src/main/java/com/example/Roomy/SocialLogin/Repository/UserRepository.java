package com.example.Roomy.SocialLogin.Repository;

import com.example.Roomy.SocialLogin.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    int countByReported(User reportedId);
}

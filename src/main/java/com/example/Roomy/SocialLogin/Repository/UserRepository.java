package com.example.Roomy.SocialLogin.Repository;

import com.example.Roomy.SocialLogin.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndSocialType(String email, String socialType);
}

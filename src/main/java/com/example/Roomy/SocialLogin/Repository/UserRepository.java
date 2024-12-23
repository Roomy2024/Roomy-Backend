package com.example.Roomy.SocialLogin.Repository;

import com.example.Roomy.SocialLogin.Entity.User;
import io.netty.util.internal.ObjectPool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialIdAndSocialType(String socialId, String socialType);
}

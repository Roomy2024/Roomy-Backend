package com.example.Roomy.SocialLogin.Repository;

import com.example.Roomy.SocialLogin.Dto.Redis.RefreshTokenDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//기본 email을 사용해 저장 및 조회
public interface RedisRepository extends CrudRepository<RefreshTokenDTO,String> {
}

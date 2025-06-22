package com.Gritty.Linki.domain.account.account.repository;

import com.Gritty.Linki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<User, String> {
    Boolean existsByUserLoginId(String userLoginId);
    User findByUserLoginId(String userLoginId);

    Optional<User> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);

    Optional<User> findByUserEmail(String userEmail);


}

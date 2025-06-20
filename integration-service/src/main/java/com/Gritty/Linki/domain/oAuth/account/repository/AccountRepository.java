package com.Gritty.Linki.domain.oAuth.account.repository;

import com.Gritty.Linki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<User, String> {
    Boolean existsByUserLoginId(String userLoginId);
    User findByUserLoginId(String userLoginId);


}

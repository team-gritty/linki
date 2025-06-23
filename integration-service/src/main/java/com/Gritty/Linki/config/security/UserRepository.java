package com.Gritty.Linki.config.security;

import com.Gritty.Linki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, String> {
        Boolean existsByUserLoginId(String userLoginId);
        Optional<User> findByUserLoginId(String userLoginId);
        @Query("SELECT u.userId FROM User u WHERE u.userLoginId = :userLoginId")
        Optional<String> findUserIdByUserLoginId(@Param("userLoginId") String userLoginId);
}

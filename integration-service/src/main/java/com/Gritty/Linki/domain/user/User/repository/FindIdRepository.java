package com.Gritty.Linki.domain.user.User.repository;

import com.Gritty.Linki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FindIdRepository extends JpaRepository<User, String> {
    
    /**
     * 이름과 이메일로 사용자 ID 조회 (대소문자, 공백 무시)
     * @param name 사용자 이름
     * @param email 사용자 이메일
     * @return 사용자 ID (없으면 null)
     */
    @Query("SELECT u.userLoginId FROM User u WHERE TRIM(LOWER(u.userName)) = TRIM(LOWER(:name)) AND TRIM(LOWER(u.userEmail)) = TRIM(LOWER(:email))")
    String findUserIdByNameAndEmail(@Param("name") String name, @Param("email") String email);
} 
package com.Gritty.Linki.domain.account.account.repository;

import com.Gritty.Linki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FindPasswordRepository extends JpaRepository<User, String> {
    
    /**
     * 아이디와 이메일로 사용자 조회 (대소문자, 공백 무시)
     * @param userLoginId 사용자 로그인 ID
     * @param userEmail 사용자 이메일
     * @return 사용자 (없으면 null)
     */
    @Query("SELECT u FROM User u WHERE TRIM(LOWER(u.userLoginId)) = TRIM(LOWER(:userLoginId)) AND TRIM(LOWER(u.userEmail)) = TRIM(LOWER(:userEmail))")
    User findByUserLoginIdAndUserEmail(@Param("userLoginId") String userLoginId, @Param("userEmail") String userEmail);
    
    /**
     * 이름, 아이디, 이메일로 사용자 조회 (대소문자, 공백 무시)
     * @param userName 사용자 이름
     * @param userLoginId 사용자 로그인 ID
     * @param userEmail 사용자 이메일
     * @return 사용자 (없으면 null)
     */
    @Query("SELECT u FROM User u WHERE TRIM(LOWER(u.userName)) = TRIM(LOWER(:userName)) AND TRIM(LOWER(u.userLoginId)) = TRIM(LOWER(:userLoginId)) AND TRIM(LOWER(u.userEmail)) = TRIM(LOWER(:userEmail))")
    User findByUserNameAndUserLoginIdAndUserEmail(@Param("userName") String userName, @Param("userLoginId") String userLoginId, @Param("userEmail") String userEmail);
} 
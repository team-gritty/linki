package com.linki.admin_integration_service.domain.account.repository;

import com.linki.admin_integration_service.entity.Admin;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Admin, String> {

    Optional<Admin> findByAdminLoginId(String adminLoginId);

    @Query("select a.adminId FROM Admin a WHERE a.adminLoginId = :adminLoginId")
    Optional<String> findAdminIdByAdminLoginId(@Param("adminLoginId") String adminLoginId);
    
    @Query("select a.adminLoginId FROM Admin a WHERE a.adminId = :adminId")
    Optional<String> findAdminLoginIdByAdminId(@Param("adminId") String adminId);


}

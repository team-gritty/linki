package com.linki.admin_integration_service.domain.operations.repository.jpa;

import com.linki.admin_integration_service.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminSignUpRepository extends JpaRepository<Admin, Long> {
    Admin findByAdminLoginId(String signUpId);
    Boolean existsByAdminLoginId(String adminLoginId);
}
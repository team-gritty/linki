package com.linki.admin_integration_service.domain.account.repository;

import com.linki.admin_integration_service.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Admin, String> {
}

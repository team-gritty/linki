package com.linki.admin_integration_service.config.token;

import com.linki.admin_integration_service.entity.Admin;
import com.linki.admin_integration_service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String > {


}

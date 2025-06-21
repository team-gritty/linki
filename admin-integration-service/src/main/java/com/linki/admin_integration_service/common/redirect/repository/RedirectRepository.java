package com.linki.admin_integration_service.common.redirect.repository;

import com.linki.admin_integration_service.entity.RedirectLinks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RedirectRepository extends JpaRepository<RedirectLinks, Long> {
    RedirectLinks findByRedirectUrl(String shortUrl);
}

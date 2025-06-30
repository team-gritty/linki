package com.linki.admin_integration_service.common.redirect.repository;

import com.linki.admin_integration_service.entity.RedirectClick;
import com.linki.admin_integration_service.entity.RedirectLinks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RedirectClickRepository extends JpaRepository<RedirectClick, Long> {
    boolean existsByRedirectLinkAndClickTime(RedirectLinks redirectLinks, LocalDate date);
    List<RedirectClick> findByRedirectLink(RedirectLinks redirectLinks);
}

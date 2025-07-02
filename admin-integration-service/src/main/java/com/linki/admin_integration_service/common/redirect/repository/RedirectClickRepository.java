package com.linki.admin_integration_service.common.redirect.repository;

import com.linki.admin_integration_service.entity.RedirectClick;
import com.linki.admin_integration_service.entity.RedirectLinks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RedirectClickRepository extends JpaRepository<RedirectClick, Long> {
    boolean existsByRedirectLinkAndClickTime(RedirectLinks redirectLinks, LocalDate date);
    List<RedirectClick> findByRedirectLink(RedirectLinks redirectLinks);
    @Query(value = """
        SELECT COALESCE(AVG(daily_count), 0)
        FROM (
            SELECT DATE(click_time) AS click_date, COUNT(*) AS daily_count
            FROM redirect_click
            GROUP BY DATE(click_time)
        ) AS daily_stats
    """, nativeQuery = true)
    double getAverageDailyClickCount();
}

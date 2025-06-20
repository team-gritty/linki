package com.linki.admin_integration_service.domain.linkiscore.repository;

import com.linki.admin_integration_service.entity.RedirectClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RedirectClickRepository extends JpaRepository<RedirectClick, Long> {

    @Query(value = """
        SELECT AVG(daily_count)
        FROM (
            SELECT DATE(click_time) AS click_date, COUNT(*) AS daily_count
            FROM redirect_click
            GROUP BY DATE(click_time)
        ) AS daily_stats
    """, nativeQuery = true)
    double getAverageDailyClickCount();
}
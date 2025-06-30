package com.linki.admin_integration_service.common.redirect.repository;

import com.linki.admin_integration_service.entity.RedirectLinks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RedirectRepository extends JpaRepository<RedirectLinks, String> {
    RedirectLinks findByRedirectUrl(String shortUrl);

    @Query("SELECT COUNT(r) FROM RedirectLinks r WHERE r.contract.proposal.influencer.influencerId = :influencerId")
    long countByInfluencerId(@Param("influencerId") String influencerId);

}

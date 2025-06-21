package com.linki.admin_integration_service.domain.linkiscore.repository;

import com.linki.admin_integration_service.entity.LinkiScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkiScoreRepository extends JpaRepository<LinkiScore,String> {
    LinkiScore findByInfluencerId(String  influencerId);
    boolean existsByInfluencerId(String  influencerId);


}

package com.Gritty.Linki.domain.user.advertiser.url.repository;

import com.Gritty.Linki.entity.RedirectLinks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<RedirectLinks, String> {
    boolean existsByRedirectUrl(String url);
    RedirectLinks findByRedirectUrl(String url);
}

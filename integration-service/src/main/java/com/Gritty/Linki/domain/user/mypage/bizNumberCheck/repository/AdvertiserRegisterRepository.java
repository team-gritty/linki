package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.repository;

import com.Gritty.Linki.entity.Advertiser;
import com.Gritty.Linki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertiserRegisterRepository extends JpaRepository<Advertiser, String > {
}

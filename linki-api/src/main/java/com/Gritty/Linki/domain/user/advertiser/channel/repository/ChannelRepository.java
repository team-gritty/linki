package com.Gritty.Linki.domain.user.advertiser.channel.repository;

import com.Gritty.Linki.domain.user.advertiser.channel.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Optional<Channel> findByChannelId(String channelId);

    boolean existsByChannelId(String channelId);

    List<Channel> findByCategory(String category);
}
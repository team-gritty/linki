package com.Gritty.Linki.repository;

import com.Gritty.Linki.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, String > {
}

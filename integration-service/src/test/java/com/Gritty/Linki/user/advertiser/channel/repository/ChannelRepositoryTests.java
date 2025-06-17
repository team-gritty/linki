package com.Gritty.Linki.user.advertiser.channel.repository;

import com.Gritty.Linki.domain.user.advertiser.repository.jpa.ChannelJpaRepository;
import com.Gritty.Linki.entity.Channel;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@SpringBootTest
@Log4j2
public class ChannelRepositoryTests {

    @Autowired
    private ChannelJpaRepository channelRepository;

    @Test
    @DisplayName("채널 목록 Repository Test")
    public void getChannelRepository() {
        List<Channel> channels = channelRepository.findAll();
        for (Channel channel : channels) {
            log.info(channel.toString());
        }
    }

    @Test
    @DisplayName("채널 검색 테스트")
    public void searchChannelRepository() {
        Pageable pageable = PageRequest.of(0, 10);

        // keyword만 있는 경우
        log.info("keyword만 있는 경우");
        Page<Channel> channels1 = channelRepository.searchChannels(
                null, "channel", null, null, null, null, pageable);
        log.info("List : {}", channels1.getContent().size());

        // category만 있는 경우
        log.info("category만 있는 경우");
        Page<Channel> channels2 = channelRepository.searchChannels(
                "게임", null, null, null, null, null, pageable);
        log.info("List : {}", channels2.getContent().size());

        // keyword, category 둘다 없는 경우
        log.info("둘다 없는 경우");
        Page<Channel> channels3 = channelRepository.searchChannels(
                null, null, null, null, null, null, pageable);
        log.info("List : {}", channels3.getContent().size());

        // category = 게임 검색
        log.info("게임 카테고리 검색");
        Page<Channel> channels4 = channelRepository.searchChannels(
                "게임", "채널1", null, null, null, null, pageable);
        log.info("List : {}", channels4.getContent().size());

        // 구독자 수 범위 검색
        log.info("구독자 수 범위 검색");
        Page<Channel> channels5 = channelRepository.searchChannels(
                null, null, 1000L, 100000L, null, null, pageable);
        log.info("List : {}", channels5.getContent().size());

        // 조회수 범위 검색
        log.info("조회수 범위 검색");
        Page<Channel> channels6 = channelRepository.searchChannels(
                null, null, null, null, 10000L, 1000000L, pageable);
        log.info("List : {}", channels6.getContent().size());
    }
}
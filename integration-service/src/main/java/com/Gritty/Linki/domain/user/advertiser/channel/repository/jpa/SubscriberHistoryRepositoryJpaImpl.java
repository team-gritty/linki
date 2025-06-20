package com.Gritty.Linki.domain.user.advertiser.channel.repository.jpa;

import com.Gritty.Linki.domain.user.advertiser.channel.dto.SubscriberHistoryDto;
import com.Gritty.Linki.domain.user.advertiser.channel.entity.SubscriberHistoryEntity;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.SubscriberHistoryRepository;
import com.Gritty.Linki.entity.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 구독자 히스토리 레포지토리 JPA 구현체
 */
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class SubscriberHistoryRepositoryJpaImpl implements SubscriberHistoryRepository {

    private final SubscriberHistoryJpaRepository subscriberHistoryJpaRepository;
    private final ChannelJpaRepository channelJpaRepository;

    /**
     * 구독자 히스토리 테이블에서 튜플들 가져오기
     * @param channelId 채널 ID (String 형식)
     * @param startDate 조회 시작일
     * @return
     */
    @Override
    public List<SubscriberHistoryDto> findByChannelIdAndDateRange(String channelId, LocalDateTime startDate) {
        log.info("구독자 히스토리 조회 - channelId: {}, startDate: {}", channelId, startDate);

        List<SubscriberHistoryEntity> entities = subscriberHistoryJpaRepository
                .findHistoryByChannelAndDateRange(channelId, startDate);

        List<SubscriberHistoryDto> dtos = entities.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());

        log.info("구독자 히스토리 조회 완료 - channelId: {}, 조회된 데이터 수: {}", channelId, dtos.size());

        return dtos;
    }

    @Override
    public String save(SubscriberHistoryDto subscriberHistoryDto) {
        log.info("구독자 히스토리를 테이블에 저장 - {}", subscriberHistoryDto);

        SubscriberHistoryEntity entity = dtoToEntity(subscriberHistoryDto);

        SubscriberHistoryEntity savedEntity = subscriberHistoryJpaRepository.save(entity);

        // 저장된 히스토리의 pk 반환
        log.info("구독자 히스토리 테이블에 저장 완료 - id: {}", savedEntity.getId());

        return savedEntity.getId();
    }

    @Override
    public List<String> findChannelIdsFromNumber(Integer startNumber) {
        log.info("{}번부터 채널 ID 조회", startNumber);

        // 채널 레포에서 특정 번호(startNumber) 이후부터의 채널 아이디를 가져오기
        List<String> channelIds = channelJpaRepository.findChannelIdsFromNumber(startNumber);

        log.info("{}번부터 채널 ID 조회 완료 - 채널 수: {}", startNumber, channelIds.size());

        // 특정 번호로부터 조회된 채널 아이디들 반환
        return channelIds;
    }

    @Override
    public List<String> findAllChannelIds() {
        log.info("모든 채널 ID 조회 (Channel 테이블에서)");

        List<String> channelIds = channelJpaRepository.findAllChannelIds();

        log.info("모든 채널 ID 조회 완료 - 채널 수: {}", channelIds.size());

        return channelIds;
    }

    /**
     * Entity를 DTO로 변환
     */
    private SubscriberHistoryDto entityToDto(SubscriberHistoryEntity entity) {
        return SubscriberHistoryDto.builder()
                .id(entity.getId())
                .channelId(entity.getChannelId())
                .subscriberCount(entity.getSubscriberCount())
                .collectedAt(entity.getCollectedAt())
                .build();
    }

    /**
     * DTO를 Entity로 변환
     */
    private SubscriberHistoryEntity dtoToEntity(SubscriberHistoryDto dto) {
        String id = dto.getId();
        if (id == null || id.isEmpty()) {
            // ID가 없으면 UUID 생성
            id = "SH" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
        }

        // Channel 엔티티 조회
        Optional<Channel> channelOptional = channelJpaRepository.findById(dto.getChannelId());
        if (channelOptional.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 채널 ID: " + dto.getChannelId());
        }

        return SubscriberHistoryEntity.builder()
                .id(id)
                .channel(channelOptional.get())
                .subscriberCount(dto.getSubscriberCount())
                .collectedAt(dto.getCollectedAt())
                .build();
    }
}
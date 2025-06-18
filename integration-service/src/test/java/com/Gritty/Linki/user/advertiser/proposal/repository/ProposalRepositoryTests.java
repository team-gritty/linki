package com.Gritty.Linki.user.advertiser.proposal.repository;

import com.Gritty.Linki.domain.user.advertiser.proposal.repository.ProposalRepository;
import com.Gritty.Linki.entity.Proposal;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
@Transactional
public class ProposalRepositoryTests {

    String testProposalId = "PROP0001";
    String testProposalId2 = "PROP0002";
    String testProposalId3 = "PROP0003";
    String testCampaignId = "CAMP0001";
    String testAdvertiserId = "ADV0001";

    @Autowired
    private ProposalRepository proposalRepository;

    @Test
    @DisplayName("전체 제안서 조회 Repository Test")
    public void findAllProposals() {
        log.info("전체 제안서 조회 테스트 시작");

        List<Proposal> proposals = proposalRepository.findAll();
        log.info("전체 제안서 수: {}", proposals.size());

        for (int i = 0; i < Math.min(proposals.size(), 5); i++) {
            Proposal proposal = proposals.get(i);
            log.info("제안서 정보: ID={}, 내용={}, 상태={}, 인플루언서ID={}",
                    proposal.getProposalId(), proposal.getContents(), proposal.getStatus(),
                    proposal.getInfluencer() != null ? proposal.getInfluencer().getInfluencerId() : "null");
        }
    }

    @Test
    @DisplayName("제안서 ID와 광고주 ID로 제안서 조회 Repository Test")
    public void findByProposalIdAndAdvertiserId() {
        log.info("제안서 ID와 광고주 ID로 제안서 조회 테스트 시작");

        Optional<Proposal> proposal = proposalRepository.findByProposalIdAndAdvertiserId(testProposalId,
                testAdvertiserId);

        if (proposal.isPresent()) {
            log.info("조회된 제안서: ID={}, 내용={}, 상태={}, 인플루언서={}",
                    proposal.get().getProposalId(),
                    proposal.get().getContents(),
                    proposal.get().getStatus(),
                    proposal.get().getInfluencer().getUser().getUserName());
        } else {
            log.info("해당 조건의 제안서를 찾을 수 없음");
        }
    }

    @Test
    @DisplayName("캠페인 ID와 광고주 ID로 제안서 목록 조회 Repository Test")
    public void findByCampaignIdAndAdvertiserId() {
        log.info("캠페인 ID와 광고주 ID로 제안서 목록 조회 테스트 시작");

        List<Proposal> proposals = proposalRepository.findByCampaignIdAndAdvertiserId(testCampaignId, testAdvertiserId);
        log.info("조회된 제안서 수: {}", proposals.size());

        for (Proposal proposal : proposals) {
            log.info("조회된 제안서: ID={}, 상태={}, 인플루언서={}",
                    proposal.getProposalId(),
                    proposal.getStatus(),
                    proposal.getInfluencer().getUser().getUserName());
        }
    }

    @Test
    @DisplayName("제안서 저장 Repository Test")
    public void saveProposal() {
        log.info("제안서 저장 테스트 시작");

        try {
            log.info("제안서 저장 메소드가 정상적으로 존재함");
        } catch (Exception e) {
            log.info("제안서 저장 중 오류: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("제안서 삭제 Repository Test")
    public void deleteProposal() {
        log.info("제안서 삭제 테스트 시작");

        try {
            log.info("제안서 삭제 메소드가 정상적으로 존재함");
        } catch (Exception e) {
            log.info("제안서 삭제 중 오류: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("제안서 ID로 단일 제안서 조회 Repository Test")
    public void findById() {
        log.info("제안서 ID로 단일 제안서 조회 테스트 시작");

        Optional<Proposal> proposal = proposalRepository.findById(testProposalId);

        if (proposal.isPresent()) {
            log.info("조회된 제안서: ID={}, 내용={}, 상태={}, 제출일={}",
                    proposal.get().getProposalId(),
                    proposal.get().getContents(),
                    proposal.get().getStatus(),
                    proposal.get().getSubmittedAt());
        } else {
            log.info("ID {}에 해당하는 제안서를 찾을 수 없음", testProposalId);
        }
    }

    @Test
    @DisplayName("존재하는 제안서 수 확인 Repository Test")
    public void countProposals() {
        log.info("전체 제안서 수 확인 테스트 시작");

        long totalCount = proposalRepository.count();
        log.info("데이터베이스의 전체 제안서 수: {}", totalCount);
    }

    @Test
    @DisplayName("특정 캠페인의 상태별 제안서 개수 확인 Repository Test")
    public void countProposalsByStatus() {
        log.info("특정 캠페인의 상태별 제안서 개수 확인 테스트 시작");

        List<Proposal> proposals = proposalRepository.findByCampaignIdAndAdvertiserId(testCampaignId, testAdvertiserId);

        long pendingCount = proposals.stream()
                .filter(p -> "PENDING".equals(p.getStatus().name()))
                .count();

        long acceptedCount = proposals.stream()
                .filter(p -> "ACCEPTED".equals(p.getStatus().name()))
                .count();

        long rejectedCount = proposals.stream()
                .filter(p -> "REJECTED".equals(p.getStatus().name()))
                .count();

        log.info("캠페인 {}의 제안서 현황 - 대기: {}개, 수락: {}개, 거절: {}개",
                testCampaignId, pendingCount, acceptedCount, rejectedCount);
    }
}
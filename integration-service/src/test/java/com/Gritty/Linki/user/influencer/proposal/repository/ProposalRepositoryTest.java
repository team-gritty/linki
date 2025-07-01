package com.Gritty.Linki.user.influencer.proposal.repository;

import com.Gritty.Linki.domain.user.influencer.proposal.repository.jpa.InfluencerProposalRepository;
import com.Gritty.Linki.domain.user.influencer.responseDTO.proposal.ProposalListResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)// 실제 DB 사용시
@Transactional
public class ProposalRepositoryTest {

    @Autowired
    private InfluencerProposalRepository influencerProposalRepository;

    @Test
    void testFindByInfluencerId(){
        // given
        String influencerId = "INF0001";

        // When
        List<ProposalListResponseDTO> proposals = influencerProposalRepository.findAllByInfluencerId(influencerId);

        // then
        assertThat(proposals).isNotEmpty();
        proposals.forEach(p -> {
            System.out.println("제안서 ID: " + p.getProposalId());
            System.out.println("캠페인 ID: " + p.getCampaignId());
            System.out.println("상태: " + p.getStatus());
        });

    }
}

package com.Gritty.Linki.domain.user.influencer.review.service;


import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.domain.user.influencer.requestDTO.InfAdvertiserReviewRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.ReviewableContractResponseDTO;
import com.Gritty.Linki.domain.user.influencer.review.repository.jpa.InfAdvertiserReviewRepository;
import com.Gritty.Linki.domain.user.influencer.settlement.repository.jpa.InfSettlementRepository;
import com.Gritty.Linki.entity.AdvertiserReview;
import com.Gritty.Linki.entity.Contract;
import com.Gritty.Linki.entity.Settlement;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.util.IdGenerator;
import com.Gritty.Linki.vo.enums.ContractStatus;
import com.Gritty.Linki.vo.enums.SettlementStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InfluencerReviewServiceImpl implements InfluencerReviewService {
    private final AuthenticationUtil authenticationUtil;
    private final ContractRepository contractRepository;
    private final InfSettlementRepository infSettlementRepository;
    private final InfAdvertiserReviewRepository infAdvertiserReviewRepository;
    @Override
    public void submitAdvertiserReview(InfAdvertiserReviewRequestDTO request) throws AccessDeniedException {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String influencerId = authenticationUtil.getInfluencerIdFromUserDetails(userDetails);

        // 3. 계약 조회
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new EntityNotFoundException("계약을 찾을 수 없습니다."));

        String contractInfluencerId = contract.getProposal().getInfluencer().getInfluencerId();

        if (!contractInfluencerId.equals(influencerId)) {
            throw new AccessDeniedException("본인의 계약이 아닙니다.");
        }

        if (!contract.getContractStatus().equals(ContractStatus.COMPLETED)) {
            throw new IllegalStateException("계약이 완료된 건에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        // 4. 리뷰 중복 여부 체크
        if (infAdvertiserReviewRepository.existsByContract_ContractId(request.getContractId())) {
            throw new IllegalStateException("이미 리뷰가 존재합니다.");
        }

        // 5. 정산 상태 확인
        Settlement settlement = infSettlementRepository.findByContract_ContractId(request.getContractId())
                .orElseThrow(() -> new IllegalStateException("정산 정보가 없습니다."));

        if (settlement.getSettlementStatus() != SettlementStatus.COMPLETED) {
            throw new IllegalStateException("정산이 완료된 계약에 대해서만 리뷰를 작성할 수 있습니다.");
        }



        // 6. 저장
        AdvertiserReview review = AdvertiserReview.builder()
                .advertiserReviewId(IdGenerator.advertiserReviewId())
                .contract(contract)
                .advertiserReviewScore(request.getAdvertiserReviewScore())
                .advertiserReviewComment(request.getAdvertiserReviewComment())
                .advertiserReviewCreatedAt(LocalDateTime.now())
                .visibility(true)
                .build();

        infAdvertiserReviewRepository.save(review);



    }

    @Override
    public List<ReviewableContractResponseDTO> getReviewableContracts() {
        // 1. 로그인한 인플루언서 정보 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String influencerId = authenticationUtil.getInfluencerIdFromUserDetails(userDetails);

        // 2. 해당 인플루언서의 정산 완료된 계약 목록 조회
        return contractRepository.findReviewableContractsByInfluencerId(influencerId);
    }
}

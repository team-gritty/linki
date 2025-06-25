package com.Gritty.Linki.domain.user.influencer.contract.service;


import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.proposal.repository.ProposalRepository;
import com.Gritty.Linki.domain.user.influencer.contract.UcanSign.UcanSignClient;
import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.domain.user.influencer.dto.ContractDTO;
import com.Gritty.Linki.domain.user.influencer.dto.UcanCreateDTO;
import com.Gritty.Linki.domain.user.influencer.proposal.repository.jpa.InfluencerProposalRepository;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ContractCreateRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.entity.Contract;
import com.Gritty.Linki.entity.Proposal;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.util.IdGenerator;
import com.Gritty.Linki.vo.enums.ContractStatus;
import jdk.jfr.Label;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdvertiserContractServiceImpl implements AdvertiserContractService {

    private final UcanSignClient ucanSignClient;
    private final ContractRepository contractRepository;
    private final ProposalRepository proposalRepository;
    String templateId = "1937344712655597570";
    private final AuthenticationUtil authenticationUtil;



    @Override
    @Retryable(
            value = {ConstraintViolationException.class },
            maxAttempts = 100,
            backoff = @Backoff(delay = 1000)
    )
    @Transactional
    public String CreateContract(ContractCreateRequestDTO dto, @AuthenticationPrincipal CustomUserDetails user) {

        Proposal proposal = proposalRepository.findById(dto.getProposalId()).orElse(null);
        String youtubeChannelId = "testYoutubeChannelId";

                log.info(proposal);

       if(proposal != null) {
           if (proposal.getInfluencer().getChannels().get(0).getYoutubeChannelId() != null) {
               youtubeChannelId = proposal.getInfluencer().getChannels().get(0).getYoutubeChannelId();
           }
       }

        String InflName = proposal.getInfluencer().getUser().getUserName();
        String AdName = proposal.getCampaign().getAdvertiser().getUser().getUserName();

        String InfEmail = proposal.getInfluencer().getUser().getUserEmail();
        String AdEmail = proposal.getCampaign().getAdvertiser().getUser().getUserEmail();

        String businessNumber = proposal.getCampaign().getAdvertiser().getBusinessNumber();



        // TODO
        UcanCreateDTO ucanCreateDTO = UcanCreateDTO.builder()
                .documentName("") // 문서이름
                .participant1Name(AdName) // 광고주 이름
                .participant1Email(AdEmail) // 광고주 이메일
                .participant2Name(InflName) // 인플 이름
                .participant2Email(InfEmail) // 인플 이메일
                .advertiserName(AdName) // 광고주 이름
                .influencerName(InflName) // 인플 이름
                .contractStartDateYear(String.valueOf(dto.getContractStartDate().getYear())) //계약시작연도
                .contractStartDateMonth(String.valueOf(dto.getContractStartDate().getMonth()))//계약시작월
                .contractStartDateDay(String.valueOf(dto.getContractStartDate().getDayOfMonth()))// 계약시작일
                .contractEndDateYear(String.valueOf(dto.getContractEndDate().getYear())) //계약종료연도
                .contractEndDateMonth(String.valueOf(dto.getContractEndDate().getMonth())) //계약종료월
                .contractEndDateDay(String.valueOf(dto.getContractEndDate().getDayOfMonth())) // 계약종료일
                .writtenDateYear(String.valueOf(LocalDateTime.now().getYear())) // 계약서 작성연도
                .writtenDateMonth(String.valueOf(LocalDateTime.now().getMonth())) // 계약서 작성월
                .writtenDateDay(String.valueOf(LocalDateTime.now().getDayOfMonth())) //계약서 작성일
                .businessNumber(businessNumber) // 광고주 사업자 번호
                .advertiserAddress(dto.getAdvertiserAddress()) // 광고주 주소
                .influencerAddress(dto.getInfluencerAddress()) // 인플루언서 주소
                .adDeliveryUrl(dto.getAdDeliveryUrl()) // 광고 이행 url
                .contractSpecialTerms(dto.getContractSpecialTerms()) // 특약 사항
                .influencerChannelUrl("https://youtube.com/@"+youtubeChannelId)
                .build();



        // 🔹 Step 2: 유캔사인 API 문서 생성
        String templateId = "1937344712655597570";
        // 실제 템플릿 ID로 대체
        String documentId = ucanSignClient.createContractDocument(ucanCreateDTO);
        log.info("유캔 싸인 계약 생성 성공");
        log.info("documentID : {}",documentId);

        if (documentId == null) {
            log.info("document id is null");
        }

        // 🔹 Step 3: 계약 ID 생성
        String contractId = IdGenerator.contractId();

        log.info("contractId : {}",contractId);



        // 🔹 Step 4: Contract 엔티티 생성
        Contract contract = Contract.builder()
                .documentId(documentId)
                .contractTitle("계약서제목")
                .contractId(contractId)
                .contractAmount(dto.getContractAmount())
                .contractStartDate(dto.getContractStartDate())
                .contractEndDate(dto.getContractEndDate())
                .contractStatus(ContractStatus.PENDING_SIGN)
                .contractCreatedAt(LocalDateTime.now())
                .contractCompletedAt(LocalDateTime.now())
                .proposal(proposal)
                .build();

        log.info("서비스에서 계약서 entity 생성완료");
        log.info(contract);

        contractRepository.save(contract);

        return contractId;
    }

    @Override
    public List<ContractListResponseDTO> getContractsByStatus(List<ContractStatus> statuses) {
        // 현재 로그인한 광고주의 ID 조회
        CustomUserDetails userDetails = (CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(userDetails);

        // 해당 광고주의 계약 목록 조회
        return contractRepository.findContractsByAdvertiserIdAndStatus(advertiserId, statuses);
    }


    // 광고주 계약 상세조회
    @Override
    public ContractDetailResponseDTO getContractDetail(String contractId) {
        // 현재 로그인한 광고주의 ID 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(userDetails);

        // 광고주의 계약 상세 정보 조회
        return contractRepository.findContractDetailForAdvertiser(contractId, advertiserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 계약을 찾을 수 없거나 권한이 없습니다."));
    }
    }


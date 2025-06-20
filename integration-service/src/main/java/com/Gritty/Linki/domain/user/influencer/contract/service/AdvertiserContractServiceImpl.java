package com.Gritty.Linki.domain.user.influencer.contract.service;


import com.Gritty.Linki.domain.user.advertiser.proposal.repository.ProposalRepository;
import com.Gritty.Linki.domain.user.influencer.contract.UcanSign.UcanSignClient;
import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.domain.user.influencer.dto.ContractDTO;
import com.Gritty.Linki.domain.user.influencer.proposal.repository.jpa.InfluencerProposalRepository;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ContractCreateRequestDTO;
import com.Gritty.Linki.entity.Contract;
import com.Gritty.Linki.entity.Proposal;
import com.Gritty.Linki.util.IdGenerator;
import com.Gritty.Linki.vo.enums.ContractStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdvertiserContractServiceImpl implements AdvertiserContractService {

    private final UcanSignClient ucanSignClient;
    private final ContractRepository contractRepository;
    private final ProposalRepository proposalRepository;
    String templateId = "1927294313194180609";


    public String createContract(ContractCreateRequestDTO dto){

        // 🔹 Step 1: 제안서 존재 여부 확인
        Proposal proposal = proposalRepository.findById(dto.getProposalId())
                .orElseThrow(() -> new IllegalArgumentException("제안서를 찾을 수 없습니다."));

        // 🔹 Step 2: 유캔사인 API 문서 생성
        String templateId = "1927294313194180609"; // 실제 템플릿 ID로 대체
//        String documentId = ucanSignClient.createContractDocument(dto, templateId);

        // 🔹 Step 3: 계약 ID 생성
        String contractId = IdGenerator.contractId();

        // 🔹 Step 4: Contract 엔티티 생성
        Contract contract = Contract.builder()
                .contractId(contractId)
                .contractTitle(dto.getContractTitle())
                .contractAmount(dto.getContractAmount())
                .contractStartDate(dto.getContractStartDate())
                .contractEndDate(dto.getContractEndDate())
                .contractStatus(ContractStatus.PENDING_SIGN)
                .contractCreatedAt(LocalDateTime.now())
//                .documentId(documentId)
//                .documentName(dto.getDocumentName())
                .proposal(proposal)
                .build();

        contractRepository.save(contract);

        return contractId;






    }


    @Override
    public String CreateContract(ContractCreateRequestDTO dto) {
        return "";
    }
}

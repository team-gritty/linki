package com.Gritty.Linki.domain.user.influencer.contract.webhook;

import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.SignatureRepository;
import com.Gritty.Linki.domain.user.influencer.dto.UcanSignWebhookDTO;
import com.Gritty.Linki.entity.Contract;
import com.Gritty.Linki.entity.Signature;
import com.Gritty.Linki.util.IdGenerator;
import com.Gritty.Linki.vo.enums.SignatureStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@Log4j2
public class ContractWebhookServiceImpl implements ContractWebhookService {

    private ContractRepository contractRepository;
    private SignatureRepository signatureRepository;


    @Override
    public void handleSignatureEvent(UcanSignWebhookDTO dto) {
        String documentId = dto.getDocumentId();
        Contract contract = contractRepository.findByDocumentId(documentId);

        if (contract == null) {
           throw new RuntimeException("Contract not found");
        }

        String contractId = contract.getContractId();


        Signature signature = signatureRepository.findByContract_ContractId(contractId);
        if(signature == null){
            throw new RuntimeException("Signature not found");
        }


        if (signature == null) {
            Signature newSignature = Signature.builder()
                    .signatureId(IdGenerator.signatureId())
                    .contract(contract)
                    .signatureSignedAt(LocalDateTime.now())
                    .signatureStatus(SignatureStatus.PARTIALLY_SIGNED)
                    .build();
            // TODO


        } else {
        switch (dto.getEventType()) {
            case "BOTH_SIGNED" -> signature.setSignatureStatus(SignatureStatus.BOTH_SIGNED);
            case "REJECTED" -> signature.setSignatureStatus(SignatureStatus.REJECTED);
            case "PARTIALLY_SIGNED" -> signature.setSignatureStatus(SignatureStatus.PARTIALLY_SIGNED);
            default -> log.warn("처리되지 않은 이벤트 타입: {}", dto.getEventType());
        }

        }
    }
}

package com.linki.admin_integration_service.domain.linkiscore.service;

import java.time.LocalDate;

import com.linki.admin_integration_service.domain.linkiscore.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContractCountServiceImpl implements ContractCountService {

    private final ContractRepository contractRepository;

    @Override
    @Transactional(readOnly = true)
    public double getContractScore(String influencerId) {

        int count = Math.toIntExact(contractRepository.findAll().stream()
                .filter(dto -> dto.getProposal().getInfluencer().getInfluencerId().equals(influencerId))
                .filter(dto -> {
                    LocalDate now = LocalDate.now();
                    LocalDate oneYearAgo = now.minusYears(1);
                    return dto.getContractStartDate().isAfter(oneYearAgo) ||
                                dto.getContractEndDate().isAfter(oneYearAgo);
                })
                .count());

        double result = switch (count) {
            case 0 -> 60;
            case 1 -> 70;
            case 2 -> 80;
            case 3 -> 90;
            default -> 100;
        };

        result = result * 0.2;


        return result;
    }
}

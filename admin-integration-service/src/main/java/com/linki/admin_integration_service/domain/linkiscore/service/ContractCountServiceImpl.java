package com.linki.admin_integration_service.domain.linkiscore.service;

import java.time.LocalDate;
import java.util.List;

import com.linki.admin_integration_service.domain.linkiscore.repository.ContractRepository;
import com.linki.admin_integration_service.entity.Contract;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContractCountServiceImpl implements ContractCountService {

    private final ContractRepository contractRepository;

    private volatile List<Contract> cachedContractList;
    private final Object cacheLock = new Object();

    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시
    public void resetCache() {
        synchronized (cacheLock) {
            cachedContractList = null;
        }
    }

    private List<Contract> getOrLoadContractList() {
        if (cachedContractList == null) {
            synchronized (cacheLock) {
                if (cachedContractList == null) {
                    cachedContractList = contractRepository.findAllWithProposalAndInfluencer();
                }
            }
        }
        return cachedContractList;
    }

    @Override
    @Transactional(readOnly = true)
    public double getContractScore(String influencerId) {
        List<Contract> contracts = getOrLoadContractList();

        int count = Math.toIntExact(contracts.stream()
                .filter(dto -> dto.getProposal() != null &&
                             dto.getProposal().getInfluencer() != null &&
                             influencerId.equals(dto.getProposal().getInfluencer().getInfluencerId()))
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

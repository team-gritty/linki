package com.linki.admin_integration_service.domain.linkiscore.service;

import com.linki.admin_integration_service.common.redirect.repository.RedirectRepository;
import com.linki.admin_integration_service.domain.linkiscore.repository.ContractRepository;
import com.linki.admin_integration_service.entity.Contract;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CPCServiceImpl implements CPCService {

    private final ContractRepository contractRepository;
    private final RedirectRepository redirectRepository;

    private volatile BigDecimal cachedAverageCPC;
    private volatile List<Contract> cachedContractList;
    private final Object cacheLock = new Object();

    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시
    public void resetCache() {
        synchronized (cacheLock) {
            cachedAverageCPC = null;
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
    public double getAverageCPC() {
        BigDecimal avgCPC = this.cachedAverageCPC;
        if (avgCPC != null) {
            return avgCPC.doubleValue();
        }

        synchronized (cacheLock) {
            if (this.cachedAverageCPC != null) {
                return this.cachedAverageCPC.doubleValue();
            }

            List<Contract> contractList = getOrLoadContractList();
            BigDecimal totalAdCost = contractList.stream()
                    .map(Contract::getContractAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long totalClickCount = redirectRepository.count();
            BigDecimal totalClick = new BigDecimal(totalClickCount);

            if (totalClick.compareTo(BigDecimal.ZERO) == 0) {
                this.cachedAverageCPC = BigDecimal.ZERO;
                return 0.0;
            }

            avgCPC = totalAdCost.divide(totalClick, 2, RoundingMode.HALF_UP);
            this.cachedAverageCPC = avgCPC;
            return avgCPC.doubleValue();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public double getCPCScore(String influencerId) {
        List<Contract> allContracts = getOrLoadContractList();

        List<Contract> influencerContracts = allContracts.stream()
                .filter(c -> c.getProposal().getInfluencer().getInfluencerId().equals(influencerId))
                .filter(c -> !"PENDING SIGN".equals(c.getContractStatus()))
                .toList();

        BigDecimal amount = influencerContracts.stream()
                .map(Contract::getContractAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long clickCount = redirectRepository.countByInfluencerId(influencerId);
        BigDecimal click = BigDecimal.valueOf(clickCount);

        BigDecimal cpc = BigDecimal.ZERO;
        if (click.compareTo(BigDecimal.ZERO) > 0) {
            cpc = amount.divide(click, 2, RoundingMode.HALF_UP);
        }

        BigDecimal averageCPC = BigDecimal.valueOf(getAverageCPC());
        if (averageCPC.compareTo(BigDecimal.ZERO) <= 0) {
            averageCPC = BigDecimal.ONE; // 0으로 나누는 것을 방지
        }

        BigDecimal diffRatio = cpc.divide(averageCPC, 4, RoundingMode.HALF_UP);

        BigDecimal calculatedResult;
        if (diffRatio.compareTo(BigDecimal.valueOf(0.80)) <= 0) {
            calculatedResult = BigDecimal.valueOf(100);
        } else if (diffRatio.compareTo(BigDecimal.valueOf(0.85)) <= 0) {
            calculatedResult = BigDecimal.valueOf(90);
        } else if (diffRatio.compareTo(BigDecimal.valueOf(0.90)) <= 0) {
            calculatedResult = BigDecimal.valueOf(80);
        } else if (diffRatio.compareTo(BigDecimal.valueOf(0.95)) <= 0) {
            calculatedResult = BigDecimal.valueOf(70);
        } else {
            calculatedResult = BigDecimal.valueOf(60);
        }

        BigDecimal result = calculatedResult.multiply(BigDecimal.valueOf(0.3));
        return result.setScale(0, RoundingMode.HALF_UP).doubleValue();
    }
}

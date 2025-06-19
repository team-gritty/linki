package com.linki.admin_integration_service.domain.dashboard.service;

import com.linki.admin_integration_service.domain.dashboard.dto.ContractStatusDTO;
import com.linki.admin_integration_service.domain.dashboard.repository.myBatis.ContractStatusMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class ContractStatusServiceImpl implements ContractStatusService {

    private final ContractStatusMapper statusMapper;

    @Override
    public ContractStatusDTO getContractStatus() {
        ContractStatusDTO contractStatusDTO = new ContractStatusDTO();
        contractStatusDTO.setActive(getActiveStat());
        contractStatusDTO.setCompleted(getCompleteStat());
        contractStatusDTO.setPending(getPendingStat());
        return contractStatusDTO;
    }

    private int getActiveStat(){
        return Math.toIntExact(statusMapper.getContractStatusMapper().stream()
                .filter(dto -> dto.getStartDate().isBefore(LocalDate.now()))
                .filter(dto -> dto.getEndDate().isAfter(LocalDate.now()))
                .filter(dto -> !dto.getContractStatus().equals("ACTIVE"))
                .count());
    }

    private int getCompleteStat(){

        YearMonth yearMonth = YearMonth.now();

        return Math.toIntExact(statusMapper.getContractStatusMapper().stream()
                .filter(dto -> dto.getContractStatus().equals("COMPLETED"))
                .filter(dto -> {
                    LocalDate start = dto.getStartDate();
                    LocalDate end = dto.getEndDate();
                    LocalDate firstDay = yearMonth.atDay(1);
                    LocalDate lastDay = yearMonth.atEndOfMonth();
                    return (start != null && !start.isBefore(firstDay) && !start.isAfter(lastDay)) ||
                           (end != null && !end.isBefore(firstDay) && !end.isAfter(lastDay));
                })
                .count());
    }

    private int getPendingStat(){
        YearMonth yearMonth = YearMonth.now();
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        return Math.toIntExact(statusMapper.getContractStatusMapper().stream()
                .filter(dto -> dto.getContractStatus().equals("PENDING SIGN"))
                .filter(dto -> {
                    LocalDate createdAt = dto.getContractCreatedDate();
                    return !createdAt.isBefore(firstDay) && !createdAt.isAfter(lastDay);
                })
                .count());
    }

}

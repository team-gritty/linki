package com.linki.admin_integration_service.domain.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.AdminSignUpAgreementDTO;
import com.linki.admin_integration_service.domain.operations.dto.AdminSignUpAgreementRequestDTO;
import com.linki.admin_integration_service.domain.operations.dto.AdminSignUpAgreementSearchRequestDTO;
import com.linki.admin_integration_service.domain.operations.repository.jpa.AdminSignUpRepository;
import com.linki.admin_integration_service.domain.operations.repository.myBatis.AdminSignUpAgreementMapper;
import com.linki.admin_integration_service.entity.Admin;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdminSignUpAgreementServiceImpl implements AdminSignUpAgreementService {

    @PersistenceContext
    private EntityManager em;


    private final AdminSignUpAgreementMapper adminSignUpAgreementMapper;
    private final ExcelUtil excelUtil;

    @Autowired
    private AdminSignUpRepository adminSignUpRepository;

    @Override
    public List<AdminSignUpAgreementDTO> getAdminSignUpList() {
        List<AdminSignUpAgreementDTO> result = adminSignUpAgreementMapper.getAdminSignUpList();
        if (result == null || result.isEmpty()) {
            return Collections.emptyList();
        }
        return result;
    }

    @Override
    @Transactional
    public Boolean approveAdminSignUp(AdminSignUpAgreementRequestDTO adminSignUpAgreementRequestDTO) {
        Admin admin  = adminSignUpRepository.findByAdminLoginId(adminSignUpAgreementRequestDTO.getAdminSignUpId());
        admin.setAdminStatus("AGREEMENT");

        if (adminSignUpRepository.existsByAdminLoginId(adminSignUpAgreementRequestDTO.getAdminSignUpId())) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public Boolean rejectAdminSignUp(AdminSignUpAgreementRequestDTO adminSignUpAgreementRequestDTO) {
        Admin admin = adminSignUpRepository.findByAdminLoginId(adminSignUpAgreementRequestDTO.getAdminSignUpId());
        admin.setAdminStatus("REJECTED");

        if (adminSignUpRepository.existsByAdminLoginId(adminSignUpAgreementRequestDTO.getAdminSignUpId())) {
            return false;
        }

        return true;
    }

    @Override
    public List<AdminSignUpAgreementDTO> searchAdminSignUp(AdminSignUpAgreementSearchRequestDTO adminSignUpAgreementSearchRequestDTO) {
        String keyword = adminSignUpAgreementSearchRequestDTO.getKeyword();
        String searchType = adminSignUpAgreementSearchRequestDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return adminSignUpAgreementMapper.getAdminSignUpList();
        }

        // 2. keyword는 있는데 searchType이 없으면 → 의미 없으므로 빈 리스트
        if ((searchType == null || searchType.isBlank()) && keyword != null && !keyword.isBlank()) {
            return Collections.emptyList();
        }

        // 3. keyword 없는데 searchType만 있으면 → 의미 없으므로 빈 리스트
        if ((keyword == null || keyword.isBlank())) {
            return Collections.emptyList();
        }

        // 4. 정상 검색
        adminSignUpAgreementSearchRequestDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        adminSignUpAgreementSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        log.info("검색 : {}",adminSignUpAgreementSearchRequestDTO);
        List<AdminSignUpAgreementDTO> result = adminSignUpAgreementMapper.searchAdminSignUp(adminSignUpAgreementSearchRequestDTO);
        log.info("result:{}", result);
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    @Override
    public String exportExcel() {
        List<AdminSignUpAgreementDTO> result = adminSignUpAgreementMapper.getAdminSignUpList();
        return excelUtil.exportExcel(result,AdminSignUpAgreementDTO.class,"AdminSignUpAgreementList",null);
    }
}

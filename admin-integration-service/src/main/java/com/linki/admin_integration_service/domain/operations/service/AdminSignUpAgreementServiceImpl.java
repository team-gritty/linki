package com.linki.admin_integration_service.domain.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.*;
import com.linki.admin_integration_service.domain.operations.repository.jpa.AdminSignUpRepository;
import com.linki.admin_integration_service.domain.operations.repository.myBatis.AdminSignUpAgreementMapper;
import com.linki.admin_integration_service.entity.Admin;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdminSignUpAgreementServiceImpl implements AdminSignUpAgreementService {

    @PersistenceContext
    private EntityManager em;


    private final AdminSignUpAgreementMapper adminSignUpAgreementMapper;
    private final ExcelUtil excelUtil;
    private final ModelMapper modelMapper;

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

    @Override
    public AdminSignUpKeysetResponseDTO getAdminSignUpListWithKeyset(String cursor, int size) {
        log.info("🔍 Keyset 계약 목록 조회 - cursor: {}, size: {}", cursor, size);

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<AdminSignUpAgreementDTO> adminSignUpAgreementDTOList = adminSignUpAgreementMapper.getAdminSignUpListWithKeyset(cursor, size + 1);
        return buildKeysetResponseDTO(adminSignUpAgreementDTOList, cursor, size);
    }

    @Override
    public AdminSignUpKeysetResponseDTO searchAdminSignUpWithKeyset(AdminSignUpAgreementSearchRequestDTO adminSignUpAgreementSearchRequestDTO, String cursor, int size) {
        log.info("🔍 Keyset 캠페인 검색 - searchType: {}, keyword: {}, cursor: {}, size: {}",
                adminSignUpAgreementSearchRequestDTO.getSearchType(), adminSignUpAgreementSearchRequestDTO.getKeyword(), cursor, size);

        // 검색 조건 검증 및 정규화
        String keyword = adminSignUpAgreementSearchRequestDTO.getKeyword();
        String searchType = adminSignUpAgreementSearchRequestDTO.getSearchType();

        // 빈 검색 조건 처리
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return getAdminSignUpListWithKeyset(cursor, size);
        }

        if ((searchType == null || searchType.isBlank()) || (keyword == null || keyword.isBlank())) {
            return AdminSignUpKeysetResponseDTO.builder()
                    .list(Collections.emptyList())
                    .hasNext(false)
                    .size(0)
                    .nextCursor(null)
                    .currentCursor(cursor)
                    .requestedSize(size)
                    .build();
        }

        // 검색 조건 정규화
        adminSignUpAgreementSearchRequestDTO.setSearchType(searchType.trim().toLowerCase(Locale.ROOT));
        adminSignUpAgreementSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<AdminSignUpAgreementDTO> searchResult = adminSignUpAgreementMapper.searchAdminSignUpWithKeyset(adminSignUpAgreementSearchRequestDTO, cursor, size + 1);

        // 검색 결과 필터링 (기존 로직 유지)
        List<AdminSignUpAgreementDTO> filteredResult = filterSearchResult(searchResult, adminSignUpAgreementSearchRequestDTO);

        return buildKeysetResponseDTO(filteredResult, cursor, size);
    }

    private List<AdminSignUpAgreementDTO> filterSearchResult(List<AdminSignUpAgreementDTO> searchResult, AdminSignUpAgreementSearchRequestDTO adminSignUpAgreementSearchRequestDTO) {
        if (searchResult.isEmpty()) {
            return Collections.emptyList();
        }

        String searchType = adminSignUpAgreementSearchRequestDTO.getSearchType();
        String findFirstValue = switch (searchType) {
            case "adminname" -> searchResult.get(0).getAdminName();
            case "adminemail" -> searchResult.get(0).getAdminEmail();
            case "adminphone" -> searchResult.get(0).getAdminPhone();
            default -> null;
        };

        if (findFirstValue == null) {
            return searchResult;
        }

        return searchResult.stream()
                .filter(dto -> {
                    return switch (searchType) {
                        case "adminname" -> dto.getAdminName().equals(findFirstValue);
                        case "adminemail" -> dto.getAdminEmail().equals(findFirstValue);
                        case "adminphone" -> dto.getAdminPhone().equals(findFirstValue);
                        default -> false;
                    };
                })
                .collect(Collectors.toList());
    }

    private AdminSignUpKeysetResponseDTO buildKeysetResponseDTO(List<AdminSignUpAgreementDTO> adminSignUpAgreementDTOList, String cursor, int size) {
        boolean hasNext = adminSignUpAgreementDTOList.size() > size;
        String nextCursor = null;

        // 실제 반환할 데이터 (size만큼만)
        List<AdminSignUpAgreementDTO> actualData = hasNext ?
                adminSignUpAgreementDTOList.subList(0, size) : adminSignUpAgreementDTOList;


        // 다음 cursor 설정 (마지막 데이터의 contractId)
        if (hasNext && !actualData.isEmpty()) {
            nextCursor = actualData.get(actualData.size() - 1).getAdminSignUpId();
        }

        // DTO 변환
        List<AdminSignUpAgreementResponseDTO> responseList = actualData.stream()
                .map(dto -> modelMapper.map(dto, AdminSignUpAgreementResponseDTO.class))
                .collect(Collectors.toList());

        log.info("📊 Keyset 응답 구성 완료 - 데이터 수: {}, hasNext: {}, nextCursor: {}",
                responseList.size(), hasNext, nextCursor);

        return AdminSignUpKeysetResponseDTO.builder()
                .list(responseList)
                .hasNext(hasNext)
                .size(responseList.size())
                .nextCursor(nextCursor)
                .currentCursor(cursor)
                .requestedSize(size)
                .build();
    }








}

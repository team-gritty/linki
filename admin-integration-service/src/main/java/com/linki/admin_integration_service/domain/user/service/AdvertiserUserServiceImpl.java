package com.linki.admin_integration_service.domain.user.service;

import com.linki.admin_integration_service.domain.user.dto.*;
import com.linki.admin_integration_service.domain.user.repository.myBatis.AdvertiserUserMapper;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdvertiserUserServiceImpl implements AdvertiserUserService {

    private final AdvertiserUserMapper advertiserUserMapper;
    private final ExcelUtil excelUtil;
    private final ModelMapper modelMapper;

    @Override
    public List<AdvertiserUserDTO> getAllAdvertiserUsers() {
        List<AdvertiserUserDTO> advertiserUserDTOList = advertiserUserMapper.getAllAdvertiserUsers();
        if (advertiserUserDTOList.isEmpty()) {
            return Collections.emptyList();
        }
        return advertiserUserDTOList;
    }


    @Override
    public List<AdvertiserUserDTO> searchAdvertiserUser(AdvertiserSearchRequestDTO advertiserSearchRequestDTO) {
        String keyword = advertiserSearchRequestDTO.getKeyword();
        String searchType = advertiserSearchRequestDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return advertiserUserMapper.getAllAdvertiserUsers();
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
        advertiserSearchRequestDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        advertiserSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<AdvertiserUserDTO> result = advertiserUserMapper.searchAdvertiserUser(advertiserSearchRequestDTO);
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    @Override
    public String exportExcel() {
        List<AdvertiserUserDTO> result = advertiserUserMapper.getAllAdvertiserUsers();
        return excelUtil.exportExcel(result,AdvertiserUserDTO.class,"AdvertiserList",null);
    }

    @Override
    public AdvertiserKeysetResponseDTO getAllAdvertiserUsersWithKeyset(String cursor, int size) {
        log.info("🔍 Keyset 계약 목록 조회 - cursor: {}, size: {}", cursor, size);

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<AdvertiserUserDTO> advertiserUserDTOList = advertiserUserMapper.getAllAdvertiserUsersWithKeyset(cursor, size + 1);
        return buildKeysetResponseDTO(advertiserUserDTOList, cursor, size);
    }

    @Override
    public AdvertiserKeysetResponseDTO searchAdvertiserUserWithKeyset(AdvertiserSearchRequestDTO searchDTO, String cursor, int size) {
        log.info("🔍 Keyset 캠페인 검색 - searchType: {}, keyword: {}, cursor: {}, size: {}",
                searchDTO.getSearchType(), searchDTO.getKeyword(), cursor, size);

        // 검색 조건 검증 및 정규화
        String keyword = searchDTO.getKeyword();
        String searchType = searchDTO.getSearchType();

        // 빈 검색 조건 처리
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return getAllAdvertiserUsersWithKeyset(cursor, size);
        }

        if ((searchType == null || searchType.isBlank()) || (keyword == null || keyword.isBlank())) {
            return AdvertiserKeysetResponseDTO.builder()
                    .list(Collections.emptyList())
                    .hasNext(false)
                    .size(0)
                    .nextCursor(null)
                    .currentCursor(cursor)
                    .requestedSize(size)
                    .build();
        }

        // 검색 조건 정규화
        searchDTO.setSearchType(searchType.trim().toLowerCase(Locale.ROOT));
        searchDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<AdvertiserUserDTO> searchResult = advertiserUserMapper.searchAdvertiserUserWithKeyset(searchDTO, cursor, size + 1);

        // 검색 결과 필터링 (기존 로직 유지)
        List<AdvertiserUserDTO> filteredResult = filterSearchResult(searchResult, searchDTO);

        return buildKeysetResponseDTO(filteredResult, cursor, size);
    }


    private List<AdvertiserUserDTO> filterSearchResult(List<AdvertiserUserDTO> searchResult, AdvertiserSearchRequestDTO advertiserSearchRequestDTO) {
        if (searchResult.isEmpty()) {
            return Collections.emptyList();
        }

        String searchType = advertiserSearchRequestDTO.getSearchType();
        String findFirstValue = switch (searchType) {
            case "advertiserid" -> searchResult.get(0).getUserId();
            case "name" -> searchResult.get(0).getName();
            case "email" -> searchResult.get(0).getEmail();
            case "phone" -> searchResult.get(0).getPhone();
            case "businessnumber" -> searchResult.get(0).getBusinessNumber();
            case "businessname" -> searchResult.get(0).getBusinessName();
            default -> null;
        };

        if (findFirstValue == null) {
            return searchResult;
        }

        return searchResult.stream()
                .filter(dto -> {
                    return switch (searchType) {
                        case "advertiserid" -> dto.getUserId().equals(findFirstValue);
                        case "name" -> dto.getName().equals(findFirstValue);
                        case "email" -> dto.getEmail().equals(findFirstValue);
                        case "phone" -> dto.getPhone().equals(findFirstValue);
                        case "businessnumber" -> dto.getBusinessNumber().equals(findFirstValue);
                        case "businessname" -> dto.getBusinessName().equals(findFirstValue);
                        default -> false;
                    };
                })
                .collect(Collectors.toList());
    }

    private AdvertiserKeysetResponseDTO buildKeysetResponseDTO(List<AdvertiserUserDTO> advertiserUserDTOList, String cursor, int size) {
        boolean hasNext = advertiserUserDTOList.size() > size;
        String nextCursor = null;

        // 실제 반환할 데이터 (size만큼만)
        List<AdvertiserUserDTO> actualData = hasNext ?
                advertiserUserDTOList.subList(0, size) : advertiserUserDTOList;


        // 다음 cursor 설정 (마지막 데이터의 userCursor)
        if (hasNext && !actualData.isEmpty()) {
            nextCursor = actualData.get(actualData.size() - 1).getUserCursor();
        }

        // DTO 변환
        List<AdvertiserResponseDTO> responseList = actualData.stream()
                .map(dto -> {
                    AdvertiserResponseDTO res = modelMapper.map(dto, AdvertiserResponseDTO.class);
                    res.setUserCursor(dto.getUserCursor());
                    return res;
                })
                .collect(Collectors.toList());

        log.info("📊 Keyset 응답 구성 완료 - 데이터 수: {}, hasNext: {}, nextCursor: {}",
                responseList.size(), hasNext, nextCursor);

        return AdvertiserKeysetResponseDTO.builder()
                .list(responseList)
                .hasNext(hasNext)
                .size(responseList.size())
                .nextCursor(nextCursor)
                .currentCursor(cursor)
                .requestedSize(size)
                .build();
    }

}


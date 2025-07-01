package com.linki.admin_integration_service.domain.user.service;

import com.linki.admin_integration_service.domain.user.dto.GeneralUSerDTO;
import com.linki.admin_integration_service.domain.user.dto.GeneralUserKeysetResponseDTO;
import com.linki.admin_integration_service.domain.user.dto.GeneralUserResponseDTO;
import com.linki.admin_integration_service.domain.user.dto.GeneralUserSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.repository.myBatis.GeneralUserMapper;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class GeneralUserServiceImpl implements GeneralUserService {

    private final GeneralUserMapper generalUserMapper;
    private final ExcelUtil excelUtil;
    private final ModelMapper modelMapper;

    @Override
    public List<GeneralUSerDTO> getAllGeneralUsers() {
        List<GeneralUSerDTO> generalUSerDTOList = generalUserMapper.getAllGeneralUsers();
        if(generalUserMapper.getAllGeneralUsers().isEmpty()){
            return Collections.emptyList();
        }
        log.info(generalUSerDTOList.get(1).getUserStatus());
        return generalUSerDTOList;
    }

    @Override
    public List<GeneralUSerDTO> searchGeneralUser(GeneralUserSearchRequestDTO generalUserSearchRequestDTO) {
        String keyword = generalUserSearchRequestDTO.getKeyword();
        String searchType = generalUserSearchRequestDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return generalUserMapper.getAllGeneralUsers();
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
        generalUserSearchRequestDTO.setSearchType(searchType.trim().toLowerCase(Locale.ROOT));
        generalUserSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<GeneralUSerDTO> result = generalUserMapper.searchGeneralUser(generalUserSearchRequestDTO);
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    @Override
    public String exportExcel() {
        List<GeneralUSerDTO> result = generalUserMapper.getAllGeneralUsers();
        return excelUtil.exportExcel(result,GeneralUSerDTO.class,"GeneralUserList",null);
    }

    @Override
    public GeneralUserKeysetResponseDTO getAllGeneralUsersWithKeyset(String cursor, int size) {
        log.info("🔍 Keyset 계약 목록 조회 - cursor: {}, size: {}", cursor, size);

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<GeneralUSerDTO> generalUSerDTOList = generalUserMapper.getAllGeneralUsersWithKeyset(cursor, size + 1);
        return buildKeysetResponseDTO(generalUSerDTOList, cursor, size);
    }

    @Override
    public GeneralUserKeysetResponseDTO searchGeneralUserWithKeyset(GeneralUserSearchRequestDTO searchDTO, String cursor, int size) {
        log.info("🔍 Keyset 캠페인 검색 - searchType: {}, keyword: {}, cursor: {}, size: {}",
                searchDTO.getSearchType(), searchDTO.getKeyword(), cursor, size);

        // 검색 조건 검증 및 정규화
        String keyword = searchDTO.getKeyword();
        String searchType = searchDTO.getSearchType();

        // 빈 검색 조건 처리
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return getAllGeneralUsersWithKeyset(cursor, size);
        }

        if ((searchType == null || searchType.isBlank()) || (keyword == null || keyword.isBlank())) {
            return GeneralUserKeysetResponseDTO.builder()
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
        List<GeneralUSerDTO> searchResult = generalUserMapper.searchGeneralUserWithKeyset(searchDTO, cursor, size + 1);

        // 검색 결과 필터링 (기존 로직 유지)
        List<GeneralUSerDTO> filteredResult = filterSearchResult(searchResult, searchDTO);

        return buildKeysetResponseDTO(filteredResult, cursor, size);
    }


    private List<GeneralUSerDTO> filterSearchResult(List<GeneralUSerDTO> searchResult, GeneralUserSearchRequestDTO generalUserSearchRequestDTO) {
        if (searchResult.isEmpty()) {
            return Collections.emptyList();
        }

        String searchType = generalUserSearchRequestDTO.getSearchType();
        String findFirstValue = switch (searchType) {
            case "name" -> searchResult.get(0).getName();
            case "email" -> searchResult.get(0).getEmail();
            case "phone" -> searchResult.get(0).getPhone();
            default -> null;
        };

        if (findFirstValue == null) {
            return searchResult;
        }

        return searchResult.stream()
                .filter(dto -> {
                    return switch (searchType) {
                        case "name" -> dto.getName().equals(findFirstValue);
                        case "email" -> dto.getEmail().equals(findFirstValue);
                        case "phone" -> dto.getPhone().equals(findFirstValue);
                        default -> false;
                    };
                })
                .collect(Collectors.toList());
    }

    private GeneralUserKeysetResponseDTO buildKeysetResponseDTO(List<GeneralUSerDTO> generalUSerDTOList, String cursor, int size) {
        boolean hasNext = generalUSerDTOList.size() > size;
        String nextCursor = null;

        // 실제 반환할 데이터 (size만큼만)
        List<GeneralUSerDTO> actualData = hasNext ?
                generalUSerDTOList.subList(0, size) : generalUSerDTOList;


        // 다음 cursor 설정 (마지막 데이터의 contractId)
        if (hasNext && !actualData.isEmpty()) {
            nextCursor = actualData.get(actualData.size() - 1).getUserId();
        }

        // DTO 변환
        List<GeneralUserResponseDTO> responseList = actualData.stream()
                .map(dto -> modelMapper.map(dto, GeneralUserResponseDTO.class))
                .collect(Collectors.toList());

        log.info("📊 Keyset 응답 구성 완료 - 데이터 수: {}, hasNext: {}, nextCursor: {}",
                responseList.size(), hasNext, nextCursor);

        return GeneralUserKeysetResponseDTO.builder()
                .list(responseList)
                .hasNext(hasNext)
                .size(responseList.size())
                .nextCursor(nextCursor)
                .currentCursor(cursor)
                .requestedSize(size)
                .build();
    }

}


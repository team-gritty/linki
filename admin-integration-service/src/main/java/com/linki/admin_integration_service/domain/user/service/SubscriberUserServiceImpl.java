package com.linki.admin_integration_service.domain.user.service;


import com.linki.admin_integration_service.domain.user.dto.*;
import com.linki.admin_integration_service.domain.user.repository.myBatis.SubscriberUserMapper;
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
@Log4j2
@RequiredArgsConstructor
// TODO : 구독 시작일 , 구독 종료일 추가 해야함
public class SubscriberUserServiceImpl implements SubscriberUserService {

    private final SubscriberUserMapper subscriberUserMapper;
    private final ExcelUtil excelUtil;
    private final ModelMapper modelMapper;

    @Override
    public List<SubscriberUserDTO> getAllSubscriberUsers() {
        List<SubscriberUserDTO> subscriberUserDTOList = subscriberUserMapper.getAllSubscriberUsers();
        if (subscriberUserDTOList.isEmpty()) {
            return Collections.emptyList();
        }
        return subscriberUserDTOList;
    }

    @Override
    public List<SubscriberUserDTO> searchSubscriberUser(SubscriberSearchRequestDTO subscriberSearchRequestDTO) {

        String keyword = subscriberSearchRequestDTO.getKeyword();
        String searchType = subscriberSearchRequestDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return subscriberUserMapper.getAllSubscriberUsers();
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
        subscriberSearchRequestDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        subscriberSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<SubscriberUserDTO> result = subscriberUserMapper.searchSubscriberUser(subscriberSearchRequestDTO);
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    @Override
    public String exportExcel() {
        List<SubscriberUserDTO> result = subscriberUserMapper.getAllSubscriberUsers();
        return excelUtil.exportExcel(result,SubscriberUserDTO.class,"SubscriberUserList",null);
    }

    @Override
    public SubscriberKeysetResponseDTO getAllSubscriberUsersWithKeyset(String cursor, int size) {
        log.info("🔍 Keyset 구독자 목록 조회 - cursor: {}, size: {}", cursor, size);

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<SubscriberUserDTO> subscriberUserDTOList = subscriberUserMapper.getAllSubscriberUsersWithKeyset(cursor, size + 1);
        return buildKeysetResponseDTO(subscriberUserDTOList, cursor, size);
    }

    @Override
    public SubscriberKeysetResponseDTO searchSubscriberUserWithKeyset(SubscriberSearchRequestDTO searchDTO, String cursor, int size) {
        log.info("🔍 Keyset 구독자 검색 - searchType: {}, keyword: {}, cursor: {}, size: {}",
                searchDTO.getSearchType(), searchDTO.getKeyword(), cursor, size);

        // 검색 조건 검증 및 정규화
        String keyword = searchDTO.getKeyword();
        String searchType = searchDTO.getSearchType();

        // 빈 검색 조건 처리
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return getAllSubscriberUsersWithKeyset(cursor, size);
        }

        if ((searchType == null || searchType.isBlank()) || (keyword == null || keyword.isBlank())) {
            return SubscriberKeysetResponseDTO.builder()
                    .list(Collections.emptyList())
                    .hasNext(false)
                    .size(0)
                    .nextCursor(null)
                    .currentCursor(cursor)
                    .requestedSize(size)
                    .build();
        }

        // 검색 조건 정규화
        searchDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        searchDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<SubscriberUserDTO> searchResult = subscriberUserMapper.searchSubscriberUserWithKeyset(searchDTO, cursor, size + 1);

        // 검색 결과 필터링 (기존 로직 유지)
        List<SubscriberUserDTO> filteredResult = filterSearchResult(searchResult, searchDTO);

        return buildKeysetResponseDTO(filteredResult, cursor, size);
    }

    private List<SubscriberUserDTO> filterSearchResult(List<SubscriberUserDTO> searchResult, SubscriberSearchRequestDTO subscriberSearchRequestDTO) {
        if (searchResult.isEmpty()) {
            return Collections.emptyList();
        }

        String searchType = subscriberSearchRequestDTO.getSearchType();
        String findFirstValue = switch (searchType) {
            case "userid" -> searchResult.get(0).getUserId();
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
                        case "userid" -> dto.getUserId().equals(findFirstValue);
                        case "name" -> dto.getName().equals(findFirstValue);
                        case "email" -> dto.getEmail().equals(findFirstValue);
                        case "phone" -> dto.getPhone().equals(findFirstValue);
                        default -> false;
                    };
                })
                .collect(Collectors.toList());
    }

    private SubscriberKeysetResponseDTO buildKeysetResponseDTO(List<SubscriberUserDTO> subscriberUserDTOList, String cursor, int size) {
        boolean hasNext = subscriberUserDTOList.size() > size;
        String nextCursor = null;

        // 실제 반환할 데이터 (size만큼만)
        List<SubscriberUserDTO> actualData = hasNext ?
                subscriberUserDTOList.subList(0, size) : subscriberUserDTOList;

        // 다음 cursor 설정 (마지막 데이터의 userCursor)
        if (hasNext && !actualData.isEmpty()) {
            nextCursor = actualData.get(actualData.size() - 1).getUserCursor();
        }

        // DTO 변환
        List<SubscriberUserResponseDTO> responseList = actualData.stream()
                .map(dto -> {
                    SubscriberUserResponseDTO res = modelMapper.map(dto, SubscriberUserResponseDTO.class);
                    res.setUserCursor(dto.getUserCursor());
                    return res;
                })
                .collect(Collectors.toList());

        log.info("📊 Keyset 응답 구성 완료 - 데이터 수: {}, hasNext: {}, nextCursor: {}",
                responseList.size(), hasNext, nextCursor);

        return SubscriberKeysetResponseDTO.builder()
                .list(responseList)
                .hasNext(hasNext)
                .size(responseList.size())
                .nextCursor(nextCursor)
                .currentCursor(cursor)
                .requestedSize(size)
                .build();
    }
}

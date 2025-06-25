package com.linki.admin_integration_service.domain.user.service;


import com.linki.admin_integration_service.domain.user.dto.*;
import com.linki.admin_integration_service.domain.user.repository.myBatis.InfluencerUserMapper;
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
public class InfluencerUserServiceImpl implements InfluencerUserService {

    private final InfluencerUserMapper influencerUserMapper;
    private final ExcelUtil excelUtil;
    private final ModelMapper modelMapper;

    @Override
    public List<InfluencerUserDTO> getAllInfluencerUsers() {
        List<InfluencerUserDTO> influencerUserDTOList = influencerUserMapper.getAllInfluencerUsers();
        if(influencerUserDTOList.isEmpty()){
            return Collections.emptyList();
        }
        return influencerUserDTOList;
    }

    @Override
    public List<InfluencerUserDTO> searchInfluencerUser(InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO) {
        String keyword = influencerUserSearchRequestDTO.getKeyword();
        String searchType = influencerUserSearchRequestDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return influencerUserMapper.getAllInfluencerUsers();
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
        influencerUserSearchRequestDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        influencerUserSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<InfluencerUserDTO> result = influencerUserMapper.searchInfluencerUser(influencerUserSearchRequestDTO);
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    @Override
    public String exportExcel() {
        List<InfluencerUserDTO> result = influencerUserMapper.getAllInfluencerUsers();
        return excelUtil.exportExcel(result,InfluencerUserDTO.class,"InfluencerUserList",null);
    }

    @Override
    public InfluencerKeysetResponseDTO getAllInfluencerUsersWithKeyset(String cursor, int size) {
        log.info("🔍 Keyset 인플루언서 목록 조회 - cursor: {}, size: {}", cursor, size);

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<InfluencerUserDTO> influencerUserDTOList = influencerUserMapper.getAllInfluencerUsersWithKeyset(cursor, size + 1);
        return buildKeysetResponseDTO(influencerUserDTOList, cursor, size);
    }

    @Override
    public InfluencerKeysetResponseDTO searchInfluencerUserWithKeyset(InfluencerUserSearchRequestDTO searchDTO, String cursor, int size) {
        log.info("🔍 Keyset 인플루언서 검색 - searchType: {}, keyword: {}, cursor: {}, size: {}",
                searchDTO.getSearchType(), searchDTO.getKeyword(), cursor, size);

        // 검색 조건 검증 및 정규화
        String keyword = searchDTO.getKeyword();
        String searchType = searchDTO.getSearchType();

        // 빈 검색 조건 처리
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return getAllInfluencerUsersWithKeyset(cursor, size);
        }

        if ((searchType == null || searchType.isBlank()) || (keyword == null || keyword.isBlank())) {
            return InfluencerKeysetResponseDTO.builder()
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
        List<InfluencerUserDTO> searchResult = influencerUserMapper.searchInfluencerUserWithKeyset(searchDTO, cursor, size + 1);

        // 검색 결과 필터링 (기존 로직 유지)
        List<InfluencerUserDTO> filteredResult = filterSearchResult(searchResult, searchDTO);

        return buildKeysetResponseDTO(filteredResult, cursor, size);
    }

    private List<InfluencerUserDTO> filterSearchResult(List<InfluencerUserDTO> searchResult, InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO) {
        if (searchResult.isEmpty()) {
            return Collections.emptyList();
        }

        String searchType = influencerUserSearchRequestDTO.getSearchType();
        String findFirstValue = switch (searchType) {
            case "userid" -> searchResult.get(0).getUserId();
            case "name" -> searchResult.get(0).getName();
            case "email" -> searchResult.get(0).getEmail();
            case "phone" -> searchResult.get(0).getPhone();
            case "snschannelname" -> searchResult.get(0).getSnsChannelName();
            case "snslink" -> searchResult.get(0).getSnsLink();
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
                        case "snschannelname" -> dto.getSnsChannelName().equals(findFirstValue);
                        case "snslink" -> dto.getSnsLink().equals(findFirstValue);
                        default -> false;
                    };
                })
                .collect(Collectors.toList());
    }

    private InfluencerKeysetResponseDTO buildKeysetResponseDTO(List<InfluencerUserDTO> influencerUserDTOList, String cursor, int size) {
        boolean hasNext = influencerUserDTOList.size() > size;
        String nextCursor = null;

        // 실제 반환할 데이터 (size만큼만)
        List<InfluencerUserDTO> actualData = hasNext ?
                influencerUserDTOList.subList(0, size) : influencerUserDTOList;

        // 다음 cursor 설정 (마지막 데이터의 userCursor)
        if (hasNext && !actualData.isEmpty()) {
            nextCursor = actualData.get(actualData.size() - 1).getUserCursor();
        }

        // DTO 변환
        List<InfluencerUserResponseDTO> responseList = actualData.stream()
                .map(dto -> {
                    InfluencerUserResponseDTO res = modelMapper.map(dto, InfluencerUserResponseDTO.class);
                    res.setUserCursor(dto.getUserCursor());
                    return res;
                })
                .collect(Collectors.toList());

        log.info("📊 Keyset 응답 구성 완료 - 데이터 수: {}, hasNext: {}, nextCursor: {}",
                responseList.size(), hasNext, nextCursor);

        return InfluencerKeysetResponseDTO.builder()
                .list(responseList)
                .hasNext(hasNext)
                .size(responseList.size())
                .nextCursor(nextCursor)
                .currentCursor(cursor)
                .requestedSize(size)
                .build();
    }
}

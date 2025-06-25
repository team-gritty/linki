package com.linki.admin_integration_service.domain.contract.service;

import com.linki.admin_integration_service.domain.contract.dto.CampaignDTO;
import com.linki.admin_integration_service.domain.contract.dto.CampaignKeysetResponseDTO;
import com.linki.admin_integration_service.domain.contract.dto.CampaignResponseDTO;
import com.linki.admin_integration_service.domain.contract.dto.CampaignSearchDTO;
import com.linki.admin_integration_service.domain.contract.repository.myBatis.CampaignMapper;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class CampaignServiceImpl implements CampaignService {

    private final CampaignMapper campaignMapper;
    private final ExcelUtil excelUtil;
    // ModelMapper는 직접 생성
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<CampaignDTO> getCampaigns() {
        List<CampaignDTO> campaignDTOS = campaignMapper.getCampaigns();

        if (campaignDTOS.isEmpty()) {
            return Collections.emptyList();
        }

        List<CampaignDTO> result =
                campaignDTOS.stream().
                map(
                        dto ->
                                dto.toBuilder().campaignLink("https://www.linki.kr/campaign/"+dto.getCampaignId()).build())
                .toList();

        return result;
    }

    @Override
    public List<CampaignDTO> searchCampaign(CampaignSearchDTO campaignSearchDTO) {
        String keyword = campaignSearchDTO.getKeyword();
        String searchType = campaignSearchDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return campaignMapper.getCampaigns();
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
        campaignSearchDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        campaignSearchDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<CampaignDTO> searchResult = campaignMapper.searchCampaign(campaignSearchDTO);
        List<CampaignDTO> streamResult = new ArrayList<>();

        if (!searchResult.isEmpty()) {
            String findSearchType = campaignSearchDTO.getSearchType();
            String findFirstValue = switch (findSearchType) {
                case "campaignid" -> searchResult.get(0).getCampaignId();
                case "advertisername" -> searchResult.get(0).getAdvertiserName();
                case "businessnumber" -> searchResult.get(0).getBusinessNumber();
                case "phone" -> searchResult.get(0).getPhone();
                case "campaigntitle" -> searchResult.get(0).getCampaignTitle();
                default -> null;
            };
            if (findFirstValue != null) {
                streamResult = searchResult.stream()
                        .filter(dto -> {
                            return switch (findSearchType) {
                                case "campaignid" -> dto.getCampaignId().equals(findFirstValue);
                                case "advertisername" -> dto.getAdvertiserName().equals(findFirstValue);
                                case "businessnumber" -> dto.getBusinessNumber().equals(findFirstValue);
                                case "phone" -> dto.getPhone().equals(findFirstValue);
                                case "campaigntitle" -> dto.getCampaignTitle().equals(findFirstValue);
                                default -> false;
                            };
                        })
                        .toList();
            }
        }

        List<CampaignDTO> result =
                streamResult.stream().
                        map(
                                dto ->
                                        dto.toBuilder().campaignLink("https://www.linki.kr/campaign/"+dto.getCampaignId()).build())
                        .toList();

        log.info("서비스 최종 검색어 searchType : {} 검색어 : {}",campaignSearchDTO.getSearchType(),campaignSearchDTO.getKeyword());
        log.info("result:{}", result);

        return result.isEmpty() ? Collections.emptyList() : result;
    }

    @Override
    public String exportExcel() {
        List<CampaignDTO> campaignDTOS = campaignMapper.getCampaigns();
        return excelUtil.exportExcel(campaignDTOS, CampaignDTO.class, "campaignList",null);
    }

    // ==============================================
    // Keyset 페이지네이션 구현
    // ==============================================
    
    /**
     * Keyset 방식으로 캠페인 목록을 조회합니다.
     * @param cursor 마지막으로 조회한 campaignId (null이면 처음부터)
     * @param size 조회할 데이터 수
     * @return 다음 페이지 정보와 함께 CampaignKeysetResponseDTO 반환
     */
    @Override
    public CampaignKeysetResponseDTO getCampaignsWithKeyset(String cursor, int size) {
        log.info("🔍 Keyset 캠페인 목록 조회 - cursor: {}, size: {}", cursor, size);

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<CampaignDTO> campaignDTOList = campaignMapper.getCampaignsWithKeyset(cursor, size + 1);

        return buildKeysetResponse(campaignDTOList, cursor, size);
    }

    /**
     * Keyset 방식으로 캠페인을 검색합니다.
     * 검색 조건이 비어 있으면 전체 캠페인 조회 로직을 사용합니다.
     * @param campaignSearchDTO 검색 조건 (searchType, keyword)
     * @param cursor 마지막으로 조회한 campaignId
     * @param size 조회할 데이터 수
     * @return 검색 결과 및 페이지 정보 포함 CampaignKeysetResponseDTO
     */
    @Override
    public CampaignKeysetResponseDTO searchCampaignWithKeyset(CampaignSearchDTO campaignSearchDTO, String cursor, int size) {
        log.info("🔍 Keyset 캠페인 검색 - searchType: {}, keyword: {}, cursor: {}, size: {}",
                campaignSearchDTO.getSearchType(), campaignSearchDTO.getKeyword(), cursor, size);

        // 검색 조건 검증 및 정규화
        String keyword = campaignSearchDTO.getKeyword();
        String searchType = campaignSearchDTO.getSearchType();

        // 빈 검색 조건 처리
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return getCampaignsWithKeyset(cursor, size);
        }

        if ((searchType == null || searchType.isBlank()) || (keyword == null || keyword.isBlank())) {
            return CampaignKeysetResponseDTO.builder()
                    .list(Collections.emptyList())
                    .hasNext(false)
                    .size(0)
                    .nextCursor(null)
                    .currentCursor(cursor)
                    .requestedSize(size)
                    .build();
        }

        // 검색 조건 정규화
        campaignSearchDTO.setSearchType(searchType.trim().toLowerCase(Locale.ROOT));
        campaignSearchDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<CampaignDTO> searchResult = campaignMapper.searchCampaignWithKeyset(campaignSearchDTO, cursor, size + 1);

        // 검색 결과 필터링 (기존 로직 유지)
        List<CampaignDTO> filteredResult = filterSearchResult(searchResult, campaignSearchDTO);

        return buildKeysetResponse(filteredResult, cursor, size);
    }

    /**
     * 검색된 캠페인 리스트에서 첫 번째 항목 기준으로 동일한 값을 가지는 항목만 필터링합니다.
     * (예: advertiserName이 동일한 항목만)
     * @param searchResult 전체 검색 결과
     * @param campaignSearchDTO 검색 조건
     * @return 필터링된 캠페인 리스트
     */
    private List<CampaignDTO> filterSearchResult(List<CampaignDTO> searchResult, CampaignSearchDTO campaignSearchDTO) {
        if (searchResult.isEmpty()) {
            return Collections.emptyList();
        }

        String searchType = campaignSearchDTO.getSearchType();
        String findFirstValue = switch (searchType) {
            case "campaignid" -> searchResult.get(0).getCampaignId();
            case "advertisername" -> searchResult.get(0).getAdvertiserName();
            case "businessnumber" -> searchResult.get(0).getBusinessNumber();
            case "phone" -> searchResult.get(0).getPhone();
            case "campaigntitle" -> searchResult.get(0).getCampaignTitle();
            default -> null;
        };

        if (findFirstValue == null) {
            return searchResult;
        }

        return searchResult.stream()
                .filter(dto -> {
                    return switch (searchType) {
                        case "campaignid" -> dto.getCampaignId().equals(findFirstValue);
                        case "advertisername" -> dto.getAdvertiserName().equals(findFirstValue);
                        case "businessnumber" -> dto.getBusinessNumber().equals(findFirstValue);
                        case "phone" -> dto.getPhone().equals(findFirstValue);
                        case "campaigntitle" -> dto.getCampaignTitle().equals(findFirstValue);
                        default -> false;
                    };
                })
                .collect(Collectors.toList());
    }

    /**
     * Keyset 응답 객체를 구성합니다.
     * - 요청된 size + 1만큼의 데이터를 받아와 다음 페이지 존재 여부를 판단
     * - 실제 반환할 데이터는 size 개수로 제한
     * - campaignLink 필드 추가 및 DTO 매핑 처리
     * @param campaignDTOList 조회된 캠페인 데이터
     * @param cursor 요청 시 전달된 현재 cursor
     * @param size 요청된 데이터 수
     * @return Keyset 기반 응답 객체
     */
    private CampaignKeysetResponseDTO buildKeysetResponse(List<CampaignDTO> campaignDTOList, String cursor, int size) {
        boolean hasNext = campaignDTOList.size() > size;
        String nextCursor = null;

        // 실제 반환할 데이터 (size만큼만)
        List<CampaignDTO> actualData = hasNext ?
                campaignDTOList.subList(0, size) : campaignDTOList;

        // 캠페인 링크 추가
        List<CampaignDTO> dataWithLinks = actualData.stream()
                .map(dto -> dto.toBuilder().campaignLink("https://www.linki.kr/campaign/" + dto.getCampaignId()).build())
                .collect(Collectors.toList());

        // 다음 cursor 설정 (마지막 데이터의 campaignId)
        if (hasNext && !dataWithLinks.isEmpty()) {
            nextCursor = dataWithLinks.get(dataWithLinks.size() - 1).getCampaignId();
        }

        // DTO 변환
        List<CampaignResponseDTO> responseList = dataWithLinks.stream()
                .map(dto -> modelMapper.map(dto, CampaignResponseDTO.class))
                .collect(Collectors.toList());

        log.info("📊 Keyset 응답 구성 완료 - 데이터 수: {}, hasNext: {}, nextCursor: {}",
                responseList.size(), hasNext, nextCursor);

        return CampaignKeysetResponseDTO.builder()
                .list(responseList)
                .hasNext(hasNext)
                .size(responseList.size())
                .nextCursor(nextCursor)
                .currentCursor(cursor)
                .requestedSize(size)
                .build();
    }
}

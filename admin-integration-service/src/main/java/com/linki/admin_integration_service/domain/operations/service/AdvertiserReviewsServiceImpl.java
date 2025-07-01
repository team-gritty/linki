package com.linki.admin_integration_service.domain.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.*;
import com.linki.admin_integration_service.domain.operations.repository.myBatis.AdvertiserReviewsMapper;
import com.linki.admin_integration_service.entity.AdvertiserReview;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdvertiserReviewsServiceImpl implements  AdvertiserReviewsService {

    @PersistenceContext
    private EntityManager em;

    private final AdvertiserReviewsMapper advertiserReviewsMapper;
    private final ExcelUtil excelUtil;

    private final ModelMapper modelMapper;

    @Override
    public List<AdvertiserReviewDTO> getAllAdvertiserReviews() {
        List<AdvertiserReviewDTO> advertiserReviewDTOS = advertiserReviewsMapper.getAllAdvertiserReviews();

        if(advertiserReviewDTOS.isEmpty()){
            return Collections.emptyList();
        }

        return advertiserReviewDTOS;
    }

    @Override
    @Transactional
    public Boolean updateAdvertiserReviewVisibility(AdvertiserReviewVisibilityRequestDTO advertiserReviewVisibilityRequestDTO) {
        AdvertiserReview advertiserReview = em.find(AdvertiserReview.class, advertiserReviewVisibilityRequestDTO.getId());

        if(advertiserReview == null){
            return false;
        }

        advertiserReview.setVisibility(advertiserReviewVisibilityRequestDTO.getVisibility());
        em.merge(advertiserReview);

        return true;
    }

    @Override
    public List<AdvertiserReviewDTO> searchAllAdvertiserReviews(AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO) {
        String keyword = advertiserReviewSearchRequestDTO.getKeyword();
        String searchType = advertiserReviewSearchRequestDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return advertiserReviewsMapper.getAllAdvertiserReviews();
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
        advertiserReviewSearchRequestDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        advertiserReviewSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<AdvertiserReviewDTO> result = advertiserReviewsMapper.searchAdvertiserReviews(advertiserReviewSearchRequestDTO);
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    @Override
    public String exportExcel() {
        List<AdvertiserReviewDTO> result = advertiserReviewsMapper.getAllAdvertiserReviews();
        return excelUtil.exportExcel(result,AdvertiserReviewDTO.class,"AdvertiserReviewList",null);
    }

    @Override
    public AdvertiserReviewKeysetResponseDTO getAllAdvertiserReviewsWithKeyset(String cursor, int size) {
        log.info("🔍 Keyset 광고주 리뷰 목록 조회 - cursor: {}, size: {}", cursor, size);

        // 1단계: 기본 리뷰 정보 빠르게 조회 (size + 1로 조회해서 다음 페이지 존재 여부 확인)
        List<AdvertiserReviewDTO> advertiserReviewDTOList = advertiserReviewsMapper.getAllAdvertiserReviewsWithKeyset(cursor, size + 1);
        
        // 2단계: 상세 정보 조회 및 병합
        if (!advertiserReviewDTOList.isEmpty()) {
            // 계약 ID 목록 추출
            List<String> contractIds = advertiserReviewDTOList.stream()
                .map(AdvertiserReviewDTO::getContractId)
                .collect(Collectors.toList());
            
            // 상세 정보 조회
            List<Map<String, Object>> reviewDetails = advertiserReviewsMapper.getReviewDetailsByContractIds(contractIds);
            
            // 상세 정보를 Map으로 변환 (빠른 조회를 위해)
            Map<String, Map<String, Object>> detailMap = reviewDetails.stream()
                .collect(Collectors.toMap(
                    detail -> (String) detail.get("contractId"),
                    detail -> detail,
                    (existing, replacement) -> existing // 중복시 첫 번째 값 유지
                ));
            
            // 리뷰 정보에 상세 정보 병합
            advertiserReviewDTOList.forEach(review -> {
                Map<String, Object> detail = detailMap.get(review.getContractId());
                if (detail != null) {
                    review.setAdvertiser((String) detail.get("advertiser"));
                    review.setWriter((String) detail.get("writer"));
                }
            });
        }
        
        return buildKeysetResponseDTO(advertiserReviewDTOList, cursor, size);
    }

    @Override
    public AdvertiserReviewKeysetResponseDTO searchAllAdvertiserReviewsWithKeyset(AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO, String cursor, int size) {
        log.info("🔍 Keyset 광고주 리뷰 검색 - searchType: {}, keyword: {}, cursor: {}, size: {}",
                advertiserReviewSearchRequestDTO.getSearchType(), advertiserReviewSearchRequestDTO.getKeyword(), cursor, size);

        // 검색 조건 검증 및 정규화
        String keyword = advertiserReviewSearchRequestDTO.getKeyword();
        String searchType = advertiserReviewSearchRequestDTO.getSearchType();

        // 빈 검색 조건 처리
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return getAllAdvertiserReviewsWithKeyset(cursor, size);
        }

        if ((searchType == null || searchType.isBlank()) || (keyword == null || keyword.isBlank())) {
            return AdvertiserReviewKeysetResponseDTO.builder()
                    .list(Collections.emptyList())
                    .hasNext(false)
                    .size(0)
                    .nextCursor(null)
                    .currentCursor(cursor)
                    .requestedSize(size)
                    .build();
        }

        // 검색 조건 정규화
        advertiserReviewSearchRequestDTO.setSearchType(searchType.trim().toLowerCase(Locale.ROOT));
        advertiserReviewSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<AdvertiserReviewDTO> searchResult = advertiserReviewsMapper.searchAdvertiserReviewsWithKeyset(advertiserReviewSearchRequestDTO, cursor, size + 1);

        // 검색 결과 필터링 (기존 로직 유지)
        List<AdvertiserReviewDTO> filteredResult = filterSearchResult(searchResult, advertiserReviewSearchRequestDTO);

        return buildKeysetResponseDTO(filteredResult, cursor, size);
    }


    private List<AdvertiserReviewDTO> filterSearchResult(List<AdvertiserReviewDTO> searchResult, AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO) {
        if (searchResult.isEmpty()) {
            return Collections.emptyList();
        }

        String searchType = advertiserReviewSearchRequestDTO.getSearchType();
        String findFirstValue = switch (searchType) {
            case "advertiser" -> searchResult.get(0).getAdvertiser();
            case "writer" -> searchResult.get(0).getWriter();
            case "contractId" -> searchResult.get(0).getContractId();
            default -> null;
        };

        if (findFirstValue == null) {
            return searchResult;
        }

        return searchResult.stream()
                .filter(dto -> {
                    return switch (searchType) {
                        case "advertiser" -> dto.getAdvertiser().equals(findFirstValue);
                        case "writer" -> dto.getWriter().equals(findFirstValue);
                        case "contractId" -> dto.getContractId().equals(findFirstValue);
                        default -> false;
                    };
                })
                .collect(Collectors.toList());
    }

    private AdvertiserReviewKeysetResponseDTO buildKeysetResponseDTO(List<AdvertiserReviewDTO> advertiserReviewDTOList, String cursor, int size) {
        boolean hasNext = advertiserReviewDTOList.size() > size;
        String nextCursor = null;

        // 실제 반환할 데이터 (size만큼만)
        List<AdvertiserReviewDTO> actualData = hasNext ?
                advertiserReviewDTOList.subList(0, size) : advertiserReviewDTOList;

        // 다음 cursor 설정 (마지막 데이터의 advertiserReviewId)
        if (hasNext && !actualData.isEmpty()) {
            nextCursor = actualData.get(actualData.size() - 1).getAdvertiserReviewId();
        }

        // DTO 변환
        List<AdvertiserResponseDTO> responseList = actualData.stream()
                .map(dto -> modelMapper.map(dto, AdvertiserResponseDTO.class))
                .collect(Collectors.toList());

        log.info("📊 Keyset 응답 구성 완료 - 데이터 수: {}, hasNext: {}, nextCursor: {}",
                responseList.size(), hasNext, nextCursor);

        return AdvertiserReviewKeysetResponseDTO.builder()
                .list(responseList)
                .hasNext(hasNext)
                .size(responseList.size())
                .nextCursor(nextCursor)
                .currentCursor(cursor)
                .requestedSize(size)
                .build();
    }


}


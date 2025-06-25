package com.linki.admin_integration_service.domain.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.*;
import com.linki.admin_integration_service.domain.operations.repository.myBatis.InfluencerReviewsMapper;
import com.linki.admin_integration_service.entity.InfluencerReview;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class InfluencerReviewsServiceImpl implements InfluencerReviewsService {

    private final InfluencerReviewsMapper influencerReviewsMapper;
    private final ExcelUtil excelUtil;
    private final ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager em;


    /**
     * 인플루언서 리뷰 전체 목록을 조회합니다.
     * <p>
     * 뷰 테이블(influencer_reviews_view)을 조회하여
     * 인플루언서, 광고주, 리뷰 정보가 포함된 데이터를 반환합니다.
     *
     * @return 전체 인플루언서 리뷰 DTO 리스트 (없을 경우 빈 리스트 반환)
     */
    @Override
    public List<InfluencerReviewDTO> getAllInfluencerReviews() {
        List<InfluencerReviewDTO> influencerReviewDTOs = influencerReviewsMapper.getAllInfluencerReviews();

        if(influencerReviewDTOs.isEmpty()){
            return Collections.emptyList();
        }

        return influencerReviewDTOs;
    }

    /**
     * 특정 인플루언서 리뷰의 공개 여부를 수정합니다.
     * <p>
     * 리뷰 ID로 해당 리뷰를 조회하여 visibility 필드를 갱신한 후 저장합니다.
     *
     * @param influencerReviewVisibilityRequestDTO 리뷰 ID와 수정할 공개 여부
     * @return 수정 성공 여부 (true: 성공, false: 실패)
     */
    @Override
    @Transactional
    public Boolean updateInfluencerReviewVisibility(InfluencerReviewVisibilityRequestDTO influencerReviewVisibilityRequestDTO) {

        InfluencerReview influencerReview = em.find(InfluencerReview.class, influencerReviewVisibilityRequestDTO.getId());

        if(influencerReview == null){
            return false;
        }

        influencerReview.setVisibility(influencerReviewVisibilityRequestDTO.getVisibility());
        em.merge(influencerReview);

        return true;
    }

    /**
     * 검색 조건에 따라 인플루언서 리뷰 목록을 조회합니다.
     * <p>
     * 검색어(keyword)가 비어 있거나 null인 경우 전체 리뷰를 조회하고,
     * 그렇지 않은 경우 주어진 searchType에 따라 조건 검색을 수행합니다.
     * <p>
     * searchType은 프론트에서 드롭다운으로 전달되며, 정규화(lowercase + trim) 처리 후 쿼리에 사용됩니다.
     *
     * @param influencerReviewSearchRequestDTO 검색 조건 (searchType, keyword 포함)
     * @return 필터링된 인플루언서 리뷰 DTO 리스트
     */
    @Override
    public List<InfluencerReviewDTO> searchAllInfluencerReviews(InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO) {
        String keyword = influencerReviewSearchRequestDTO.getKeyword();
        String searchType = influencerReviewSearchRequestDTO.getSearchType();

        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return influencerReviewsMapper.getAllInfluencerReviews();
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
        influencerReviewSearchRequestDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        influencerReviewSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<InfluencerReviewDTO> result = influencerReviewsMapper.searchInfluencerReviews(influencerReviewSearchRequestDTO);
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    @Override
    public String exportExcel() {
        List<InfluencerReviewDTO> result = influencerReviewsMapper.getAllInfluencerReviews();
        return excelUtil.exportExcel(result,InfluencerReviewDTO.class,"InfluencerReviewList",null);
    }

    @Override
    public InfluencerReviewKeysetResponseDTO getAllInfluencerReviewsWithKeyset(String cursor, int size) {
        log.info("🔍 Keyset 계약 목록 조회 - cursor: {}, size: {}", cursor, size);

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<InfluencerReviewDTO> influencerReviewDTOList = influencerReviewsMapper.getAllInfluencerReviewsWithKeyset(cursor, size + 1);
        return buildKeysetResponseDTO(influencerReviewDTOList, cursor, size);
    }

    @Override
    public InfluencerReviewKeysetResponseDTO searchAllInfluencerReviewsWithKeyset(InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO, String cursor, int size) {
        log.info("🔍 Keyset 캠페인 검색 - searchType: {}, keyword: {}, cursor: {}, size: {}",
                influencerReviewSearchRequestDTO.getSearchType(), influencerReviewSearchRequestDTO.getKeyword(), cursor, size);

        // 검색 조건 검증 및 정규화
        String keyword = influencerReviewSearchRequestDTO.getKeyword();
        String searchType = influencerReviewSearchRequestDTO.getSearchType();

        // 빈 검색 조건 처리
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return getAllInfluencerReviewsWithKeyset(cursor, size);
        }

        if ((searchType == null || searchType.isBlank()) || (keyword == null || keyword.isBlank())) {
            return InfluencerReviewKeysetResponseDTO.builder()
                    .list(Collections.emptyList())
                    .hasNext(false)
                    .size(0)
                    .nextCursor(null)
                    .currentCursor(cursor)
                    .requestedSize(size)
                    .build();
        }

        // 검색 조건 정규화
        influencerReviewSearchRequestDTO.setSearchType(searchType.trim().toLowerCase(Locale.ROOT));
        influencerReviewSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<InfluencerReviewDTO> searchResult = influencerReviewsMapper.searchInfluencerReviewsWithKeyset(influencerReviewSearchRequestDTO, cursor, size + 1);

        // 검색 결과 필터링 (기존 로직 유지)
        List<InfluencerReviewDTO> filteredResult = filterSearchResult(searchResult, influencerReviewSearchRequestDTO);

        return buildKeysetResponseDTO(filteredResult, cursor, size);
    }


    private List<InfluencerReviewDTO> filterSearchResult(List<InfluencerReviewDTO> searchResult, InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO) {
        if (searchResult.isEmpty()) {
            return Collections.emptyList();
        }

        String searchType = influencerReviewSearchRequestDTO.getSearchType();
        String findFirstValue = switch (searchType) {
            case "influencer" -> searchResult.get(0).getInfluencer();
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
                        case "influencer" -> dto.getInfluencer().equals(findFirstValue);
                        case "writer" -> dto.getWriter().equals(findFirstValue);
                        case "contractId" -> dto.getContractId().equals(findFirstValue);
                        default -> false;
                    };
                })
                .collect(Collectors.toList());
    }

    private InfluencerReviewKeysetResponseDTO buildKeysetResponseDTO(List<InfluencerReviewDTO> influencerReviewDTOS, String cursor, int size) {
        boolean hasNext = influencerReviewDTOS.size() > size;
        String nextCursor = null;

        // 실제 반환할 데이터 (size만큼만)
        List<InfluencerReviewDTO> actualData = hasNext ?
                influencerReviewDTOS.subList(0, size) : influencerReviewDTOS;


        // 다음 cursor 설정 (마지막 데이터의 contractId)
        if (hasNext && !actualData.isEmpty()) {
            nextCursor = actualData.get(actualData.size() - 1).getInfluencerReviewId();
        }

        // DTO 변환
        List<InfluencerResponseDTO> responseList = actualData.stream()
                .map(dto -> modelMapper.map(dto, InfluencerResponseDTO.class))
                .collect(Collectors.toList());

        log.info("📊 Keyset 응답 구성 완료 - 데이터 수: {}, hasNext: {}, nextCursor: {}",
                responseList.size(), hasNext, nextCursor);

        return InfluencerReviewKeysetResponseDTO.builder()
                .list(responseList)
                .hasNext(hasNext)
                .size(responseList.size())
                .nextCursor(nextCursor)
                .currentCursor(cursor)
                .requestedSize(size)
                .build();
    }

}

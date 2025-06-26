package com.linki.admin_integration_service.domain.payment.service;

import com.linki.admin_integration_service.domain.payment.dto.*;
import com.linki.admin_integration_service.domain.payment.repository.myBatis.SettlementMapper;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

// TODO : 정산상태(isSettle) 추가 해야함
@Service
@RequiredArgsConstructor
@Log4j2
public class SettlementServiceImpl implements SettlementService {


    private final SettlementMapper settlementMapper;
    private final ExcelUtil excelUtil;
    private final ModelMapper modelMapper;


    @Override
    public List<SettlementDTO> getAllSettlements() {
        List<SettlementDTO> settlementDTOList = settlementMapper.getAllSettlements();
        if (settlementDTOList.isEmpty()) {
            return Collections.emptyList();
        }
        List<SettlementDTO> result = settlementDTOList.stream()
                .map(dto -> dto.toBuilder().isSettled("PENDING").build())
                .toList();

        return result;
    }

    @Override
    public List<SettlementDTO> searchSettlement(SettlementSearchDTO settlementSearchDTO) {
        String keyword = settlementSearchDTO.getKeyword();
        String searchType = settlementSearchDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return settlementMapper.getAllSettlements();
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
        settlementSearchDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        settlementSearchDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<SettlementDTO> searchResult = settlementMapper.searchSettlement(settlementSearchDTO);
        List<SettlementDTO> streamResult = new ArrayList<>();

        if (!searchResult.isEmpty()) {
            String findSearchType = settlementSearchDTO.getSearchType();
            String findFirstValue = switch (findSearchType) {
                case "advertisername" -> searchResult.get(0).getAdvertiserName();
                case "influencername" -> searchResult.get(0).getInfluencerName();
                case "contractid" -> searchResult.get(0).getContractId();
                default -> null;
            };
            if (findFirstValue != null) {
                streamResult = searchResult.stream()
                        .filter(dto -> {
                            return switch (findSearchType) {
                                case "advertisername" -> dto.getAdvertiserName().equals(findFirstValue);
                                case "influencername" -> dto.getInfluencerName().equals(findFirstValue);
                                case "contractid" -> dto.getContractId().equals(findFirstValue);
                                default -> false;
                            };
                        })
                        .toList();
            }
        }

        log.info("서비스 최종 검색어 searchType : {} 검색어 : {}",settlementSearchDTO.getSearchType(),settlementSearchDTO.getKeyword());
        log.info("result:{}", streamResult);



        return streamResult.isEmpty() ? Collections.emptyList() : streamResult;
    }


    // TODO : 정산 처리 메소드(구현 필요)
    @Override
    public Boolean approveSettlement(SettlementRequestDTO settlementRequestDTO) {
        return true;
    }

    @Override
    public String exportExcel() {
        List<SettlementDTO> result = settlementMapper.getAllSettlements();
        return excelUtil.exportExcel(result,SettlementDTO.class,"SettlementList",null);
    }

    @Override
    public SettlementKeysetResponseDTO getAllSettlementsWithKeyset(String cursor, int size) {
        log.info("🔍 Keyset 계약 목록 조회 - cursor: {}, size: {}", cursor, size);

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<SettlementDTO> settlementDTOList = settlementMapper.getAllSettlementsWithKeyset(cursor, size + 1);
        return buildKeysetResponseDTO(settlementDTOList, cursor, size);
    }

    @Override
    public SettlementKeysetResponseDTO searchSettlementWithKeyset(SettlementSearchDTO searchDTO, String cursor, int size) {
        log.info("🔍 Keyset 캠페인 검색 - searchType: {}, keyword: {}, cursor: {}, size: {}",
                searchDTO.getSearchType(), searchDTO.getKeyword(), cursor, size);

        // 검색 조건 검증 및 정규화
        String keyword = searchDTO.getKeyword();
        String searchType = searchDTO.getSearchType();

        // 빈 검색 조건 처리
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return getAllSettlementsWithKeyset(cursor, size);
        }

        if ((searchType == null || searchType.isBlank()) || (keyword == null || keyword.isBlank())) {
            return SettlementKeysetResponseDTO.builder()
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
        List<SettlementDTO> searchResult = settlementMapper.searchSettlementWithKeyset(searchDTO, cursor, size + 1);

        // 검색 결과 필터링 (기존 로직 유지)
        List<SettlementDTO> filteredResult = filterSearchResult(searchResult, searchDTO);

        return buildKeysetResponseDTO(filteredResult, cursor, size);
    }


    private List<SettlementDTO> filterSearchResult(List<SettlementDTO> searchResult, SettlementSearchDTO settlementSearchDTO) {
        if (searchResult.isEmpty()) {
            return Collections.emptyList();
        }

        String searchType = settlementSearchDTO.getSearchType();
        String findFirstValue = switch (searchType) {
            case "contractid" -> searchResult.get(0).getContractId();
            case "advertisername" -> searchResult.get(0).getAdvertiserName();
            case "influencername" -> searchResult.get(0).getInfluencerName();
            default -> null;
        };

        if (findFirstValue == null) {
            return searchResult;
        }

        return searchResult.stream()
                .filter(dto -> {
                    return switch (searchType) {
                        case "contractid" -> dto.getContractId().equals(findFirstValue);
                        case "advertisername" -> dto.getAdvertiserName().equals(findFirstValue);
                        case "influencername" -> dto.getInfluencerName().equals(findFirstValue);
                        default -> false;
                    };
                })
                .collect(Collectors.toList());
    }

    private SettlementKeysetResponseDTO buildKeysetResponseDTO(List<SettlementDTO> settlementDTOList, String cursor, int size) {
        boolean hasNext = settlementDTOList.size() > size;
        String nextCursor = null;

        // 실제 반환할 데이터 (size만큼만)
        List<SettlementDTO> actualData = hasNext ?
                settlementDTOList.subList(0, size) : settlementDTOList;


        // 다음 cursor 설정 (마지막 데이터의 contractId)
        if (hasNext && !actualData.isEmpty()) {
            nextCursor = actualData.get(actualData.size() - 1).getContractId();
        }

        // DTO 변환
        List<SettlementResponseDTO> responseList = actualData.stream()
                .map(dto -> modelMapper.map(dto, SettlementResponseDTO.class))
                .collect(Collectors.toList());

        log.info("📊 Keyset 응답 구성 완료 - 데이터 수: {}, hasNext: {}, nextCursor: {}",
                responseList.size(), hasNext, nextCursor);




        return SettlementKeysetResponseDTO.builder()
                .list(responseList)
                .hasNext(hasNext)
                .size(responseList.size())
                .nextCursor(nextCursor)
                .currentCursor(cursor)
                .requestedSize(size)
                .build();
    }

}


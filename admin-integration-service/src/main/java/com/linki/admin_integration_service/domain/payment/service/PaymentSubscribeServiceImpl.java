package com.linki.admin_integration_service.domain.payment.service;

import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeDTO;
import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeKeysetResponseDTO;
import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeResponseDTO;
import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeSearchDTO;
import com.linki.admin_integration_service.domain.payment.repository.myBatis.PaymentSubscribeMapper;
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
public class PaymentSubscribeServiceImpl implements PaymentSubscribeService {

    private final PaymentSubscribeMapper paymentSubscribeMapper;
    private final ExcelUtil excelUtil;
    private final ModelMapper modelMapper;

    @Override
    public List<PaymentSubscribeDTO> getAllPaymentSubscribes() {
        List<PaymentSubscribeDTO> paymentSubscribeDTOList = paymentSubscribeMapper.getAllPaymentSubscribes();
        if (paymentSubscribeDTOList.isEmpty()) {
            return Collections.emptyList();
        }

        List<PaymentSubscribeDTO> result =
                paymentSubscribeDTOList.stream()
                        .map(
                                dto ->
                                        dto.toBuilder().memberType(dto.getMemberType().replace("ROLE_", "")).build())
                        .toList();
        return result;
    }

    @Override
    public List<PaymentSubscribeDTO> searchPaymentSubscribe(PaymentSubscribeSearchDTO searchDTO) {
        String keyword = searchDTO.getKeyword();
        String searchType = searchDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return paymentSubscribeMapper.getAllPaymentSubscribes();
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
        searchDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        searchDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<PaymentSubscribeDTO> searchResult = paymentSubscribeMapper.searchPaymentSubscribe(searchDTO);
        List<PaymentSubscribeDTO> streamResult = new ArrayList<>();

        if (!searchResult.isEmpty()) {
            String findSearchType = searchDTO.getSearchType();
            String findFirstValue = switch (findSearchType) {
                case "name" -> searchResult.get(0).getName();
                case "loginId" -> searchResult.get(0).getLoginId();
                case "phone" -> searchResult.get(0).getPhone();
                default -> null;
            };
            if (findFirstValue != null) {
                streamResult = searchResult.stream()
                        .filter(dto -> {
                            return switch (findSearchType) {
                                case "name" -> dto.getName().equals(findFirstValue);
                                case "loginId" -> dto.getLoginId().equals(findFirstValue);
                                case "phone" -> dto.getPhone().equals(findFirstValue);
                                default -> false;
                            };
                        })
                        .toList();
            }
        }

        log.info("서비스 최종 검색어 searchType : {} 검색어 : {}",searchDTO.getSearchType(),searchDTO.getKeyword());
        log.info("result:{}", streamResult);

        List<PaymentSubscribeDTO> result =
                streamResult.stream()
                        .map(
                                dto ->
                                        dto.toBuilder().memberType(dto.getMemberType().replace("ROLE_", "")).build())
                        .toList();

        return result.isEmpty() ? Collections.emptyList() : result;
    }

    @Override
    public String exportExcel() {
        List<PaymentSubscribeDTO> result = paymentSubscribeMapper.getAllPaymentSubscribes();
        return excelUtil.exportExcel(result,PaymentSubscribeDTO.class,"PaymentSubscribeList",null);
    }

    @Override
    public PaymentSubscribeKeysetResponseDTO getAllPaymentSubscribesWithKeyset(String cursor, int size) {
        log.info("🔍 Keyset 계약 목록 조회 - cursor: {}, size: {}", cursor, size);

        // size + 1로 조회해서 다음 페이지 존재 여부 확인
        List<PaymentSubscribeDTO> paymentSubscribeDTOList = paymentSubscribeMapper.getAllPaymentSubscribesWithKeyset(cursor, size + 1);
        return buildKeysetResponseDTO(paymentSubscribeDTOList, cursor, size);
    }

    @Override
    public PaymentSubscribeKeysetResponseDTO searchPaymentSubscribeWithKeyset(PaymentSubscribeSearchDTO searchDTO, String cursor, int size) {
        log.info("🔍 Keyset 캠페인 검색 - searchType: {}, keyword: {}, cursor: {}, size: {}",
                searchDTO.getSearchType(), searchDTO.getKeyword(), cursor, size);

        // 검색 조건 검증 및 정규화
        String keyword = searchDTO.getKeyword();
        String searchType = searchDTO.getSearchType();

        // 빈 검색 조건 처리
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return getAllPaymentSubscribesWithKeyset(cursor, size);
        }

        if ((searchType == null || searchType.isBlank()) || (keyword == null || keyword.isBlank())) {
            return PaymentSubscribeKeysetResponseDTO.builder()
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
        List<PaymentSubscribeDTO> searchResult = paymentSubscribeMapper.searchPaymentSubscribeWithKeyset(searchDTO, cursor, size + 1);

        // 검색 결과 필터링 (기존 로직 유지)
        List<PaymentSubscribeDTO> filteredResult = filterSearchResult(searchResult, searchDTO);

        return buildKeysetResponseDTO(filteredResult, cursor, size);
    }


    private List<PaymentSubscribeDTO> filterSearchResult(List<PaymentSubscribeDTO> searchResult, PaymentSubscribeSearchDTO paymentSubscribeSearchDTO) {
        if (searchResult.isEmpty()) {
            return Collections.emptyList();
        }

        String searchType = paymentSubscribeSearchDTO.getSearchType();
        String findFirstValue = switch (searchType) {
            case "name" -> searchResult.get(0).getName();
            case "loginId" -> searchResult.get(0).getLoginId();
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
                        case "loginId" -> dto.getLoginId().equals(findFirstValue);
                        case "phone" -> dto.getPhone().equals(findFirstValue);
                        default -> false;
                    };
                })
                .collect(Collectors.toList());
    }

    private PaymentSubscribeKeysetResponseDTO buildKeysetResponseDTO(List<PaymentSubscribeDTO> paymentSubscribeDTOList, String cursor, int size) {
        boolean hasNext = paymentSubscribeDTOList.size() > size;
        String nextCursor = null;

        // 실제 반환할 데이터 (size만큼만)
        List<PaymentSubscribeDTO> actualData = hasNext ?
                paymentSubscribeDTOList.subList(0, size) : paymentSubscribeDTOList;


        // 다음 cursor 설정 (마지막 데이터의 contractId)
        if (hasNext && !actualData.isEmpty()) {
            nextCursor = actualData.get(actualData.size() - 1).getUserId();
        }

        // DTO 변환
        List<PaymentSubscribeResponseDTO> responseList = actualData.stream()
                .map(dto -> modelMapper.map(dto, PaymentSubscribeResponseDTO.class))
                .map(
                        dto ->
                                dto.toBuilder().memberType(dto.getMemberType().replace("ROLE_", "")).build())
                .collect(Collectors.toList());

        log.info("📊 Keyset 응답 구성 완료 - 데이터 수: {}, hasNext: {}, nextCursor: {}",
                responseList.size(), hasNext, nextCursor);




        return PaymentSubscribeKeysetResponseDTO.builder()
                .list(responseList)
                .hasNext(hasNext)
                .size(responseList.size())
                .nextCursor(nextCursor)
                .currentCursor(cursor)
                .requestedSize(size)
                .build();
    }

}


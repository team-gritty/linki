package com.linki.admin_integration_service.domain.payment.service;

import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeDTO;
import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeSearchDTO;
import com.linki.admin_integration_service.domain.payment.repository.myBatis.PaymentSubscribeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentSubscribeServiceImpl implements PaymentSubscribeService {

    private final PaymentSubscribeMapper paymentSubscribeMapper;

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
}

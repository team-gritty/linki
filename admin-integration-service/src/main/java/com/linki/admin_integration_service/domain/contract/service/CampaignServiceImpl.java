package com.linki.admin_integration_service.domain.contract.service;

import com.linki.admin_integration_service.domain.contract.dto.CampaignDTO;
import com.linki.admin_integration_service.domain.contract.dto.CampaignSearchDTO;
import com.linki.admin_integration_service.domain.contract.repository.myBatis.CampaignMapper;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class CampaignServiceImpl implements CampaignService {

    private final CampaignMapper campaignMapper;
    private final ExcelUtil excelUtil;

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
}


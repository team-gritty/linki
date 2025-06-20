package com.linki.admin_integration_service.domain.user.service;

import com.linki.admin_integration_service.domain.user.dto.AdvertiserSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.dto.AdvertiserUserDTO;
import com.linki.admin_integration_service.domain.user.repository.myBatis.AdvertiserUserMapper;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdvertiserUserServiceImpl implements AdvertiserUserService {

    private final AdvertiserUserMapper advertiserUserMapper;
    private final ExcelUtil excelUtil;

    @Override
    public List<AdvertiserUserDTO> getAllAdvertiserUsers() {
        List<AdvertiserUserDTO> advertiserUserDTOList = advertiserUserMapper.getAllAdvertiserUsers();
        if (advertiserUserDTOList.isEmpty()) {
            return Collections.emptyList();
        }
        return advertiserUserDTOList;
    }


    @Override
    public List<AdvertiserUserDTO> searchAdvertiserUser(AdvertiserSearchRequestDTO advertiserSearchRequestDTO) {
        String keyword = advertiserSearchRequestDTO.getKeyword();
        String searchType = advertiserSearchRequestDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return advertiserUserMapper.getAllAdvertiserUsers();
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
        advertiserSearchRequestDTO.setSearchType(searchType.trim().toLowerCase(Locale.ROOT));
        advertiserSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<AdvertiserUserDTO> result = advertiserUserMapper.searchAdvertiserUser(advertiserSearchRequestDTO);
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    @Override
    public String exportExcel() {
        List<AdvertiserUserDTO> result = advertiserUserMapper.getAllAdvertiserUsers();
        return excelUtil.exportExcel(result,AdvertiserUserDTO.class,"AdvertiserList",null);
    }
}

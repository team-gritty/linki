package com.linki.admin_integration_service.domain.user.service;


import com.linki.admin_integration_service.domain.user.dto.InfluencerUserDTO;
import com.linki.admin_integration_service.domain.user.dto.InfluencerUserSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.repository.myBatis.InfluencerUserMapper;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class InfluencerUserServiceImpl implements InfluencerUserService {

    private final InfluencerUserMapper influencerUserMapper;
    private final ExcelUtil excelUtil;

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
}

package com.linki.admin_integration_service.domain.user.service;

import com.linki.admin_integration_service.domain.user.dto.GeneralUSerDTO;
import com.linki.admin_integration_service.domain.user.dto.GeneralUserSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.repository.myBatis.GeneralUserMapper;
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
public class GeneralUserServiceImpl implements GeneralUserService {

    private final GeneralUserMapper generalUserMapper;
    private final ExcelUtil excelUtil;

    @Override
    public List<GeneralUSerDTO> getAllGeneralUsers() {
        List<GeneralUSerDTO> generalUSerDTOList = generalUserMapper.getAllGeneralUsers();
        if(generalUserMapper.getAllGeneralUsers().isEmpty()){
            return Collections.emptyList();
        }
        log.info(generalUSerDTOList.get(1).getUserStatus());
        return generalUSerDTOList;
    }

    @Override
    public List<GeneralUSerDTO> searchGeneralUser(GeneralUserSearchRequestDTO generalUserSearchRequestDTO) {
        String keyword = generalUserSearchRequestDTO.getKeyword();
        String searchType = generalUserSearchRequestDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return generalUserMapper.getAllGeneralUsers();
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
        generalUserSearchRequestDTO.setSearchType(searchType.trim().toLowerCase(Locale.ROOT));
        generalUserSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<GeneralUSerDTO> result = generalUserMapper.searchGeneralUser(generalUserSearchRequestDTO);
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    @Override
    public String exportExcel() {
        List<GeneralUSerDTO> result = generalUserMapper.getAllGeneralUsers();
        return excelUtil.exportExcel(result,GeneralUSerDTO.class,"GeneralUserList",null);
    }
}

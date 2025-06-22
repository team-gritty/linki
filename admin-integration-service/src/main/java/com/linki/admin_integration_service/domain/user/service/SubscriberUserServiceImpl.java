package com.linki.admin_integration_service.domain.user.service;


import com.linki.admin_integration_service.domain.user.dto.SubscriberSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.dto.SubscriberUserDTO;
import com.linki.admin_integration_service.domain.user.repository.myBatis.SubscriberUserMapper;
import com.linki.admin_integration_service.util.excel.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
// TODO : 구독 시작일 , 구독 종료일 추가 해야함
public class SubscriberUserServiceImpl implements SubscriberUserService {

    private final SubscriberUserMapper subscriberUserMapper;
    private final ExcelUtil excelUtil;

    @Override
    public List<SubscriberUserDTO> getAllSubscriberUsers() {
        List<SubscriberUserDTO> subscriberUserDTOList = subscriberUserMapper.getAllSubscriberUsers();
        if (subscriberUserDTOList.isEmpty()) {
            return Collections.emptyList();
        }
        return subscriberUserDTOList;
    }

    @Override
    public List<SubscriberUserDTO> searchSubscriberUser(SubscriberSearchRequestDTO subscriberSearchRequestDTO) {

        String keyword = subscriberSearchRequestDTO.getKeyword();
        String searchType = subscriberSearchRequestDTO.getSearchType();
        log.info("keyword:{} searchType:{}", keyword, searchType);
        // 1. 둘 다 비었을 경우 → 전체 조회
        if ((keyword == null || keyword.isBlank()) && (searchType == null || searchType.isBlank())) {
            return subscriberUserMapper.getAllSubscriberUsers();
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
        subscriberSearchRequestDTO.setSearchType(Objects.requireNonNull(searchType).trim().toLowerCase(Locale.ROOT));
        subscriberSearchRequestDTO.setKeyword(keyword.trim().toLowerCase(Locale.ROOT));
        List<SubscriberUserDTO> result = subscriberUserMapper.searchSubscriberUser(subscriberSearchRequestDTO);
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    @Override
    public String exportExcel() {
        List<SubscriberUserDTO> result = subscriberUserMapper.getAllSubscriberUsers();
        return excelUtil.exportExcel(result,SubscriberUserDTO.class,"SubscriberUserList",null);
    }
}

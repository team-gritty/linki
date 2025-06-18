package com.linki.admin_integration_service.user.repository;

import com.linki.admin_integration_service.config.MyBatisConfig;
import com.linki.admin_integration_service.domain.user.dto.InfluencerUserDTO;
import com.linki.admin_integration_service.domain.user.dto.InfluencerUserSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.repository.myBatis.InfluencerUserMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@SpringBootTest
@Import(MyBatisConfig.class)
@Log4j2
public class InfluencerUserMapperTest {

    @Autowired
    private InfluencerUserMapper influencerUserMapper;

    @Test
    @DisplayName("인플루언서 회원 검색 Mapepr Test")
    public void getAllInfluencerUserTest(){
        List<InfluencerUserDTO> influencerUserDTOList = influencerUserMapper.getAllInfluencerUsers();

        log.info("인플루언서 회원수 : {}", influencerUserDTOList.size());

    }

    @Test
    @DisplayName("인플루언서 회원 검색 Mapper Test")
    public void searchInfluencerUserTest(){

        // 둘다 빈 경우
        InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO = new InfluencerUserSearchRequestDTO();
        influencerUserSearchRequestDTO.setKeyword("");
        influencerUserSearchRequestDTO.setSearchType("");
        List<InfluencerUserDTO> influencerUserDTOList = influencerUserMapper.searchInfluencerUser(influencerUserSearchRequestDTO);
        log.info("둘다 비었을 경우  : {} , 검색어 : {} 키워드 : {} ", influencerUserDTOList.size(), influencerUserDTOList.size(), influencerUserDTOList.size());

        // keyword 빈 경우
        InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO2 = new InfluencerUserSearchRequestDTO();
        influencerUserSearchRequestDTO2.setKeyword("");
        influencerUserSearchRequestDTO2.setSearchType("name");
        List<InfluencerUserDTO> influencerUserDTOList2 = influencerUserMapper.searchInfluencerUser(influencerUserSearchRequestDTO2);
        log.info("keyword 비었을 경우  : {} , searchType : {} , keyword : {}", influencerUserDTOList2.size(), influencerUserSearchRequestDTO2.getSearchType(), influencerUserSearchRequestDTO2.getKeyword());

        // searchType 빈 경우
        InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO3 = new InfluencerUserSearchRequestDTO();
        influencerUserSearchRequestDTO3.setKeyword("홍길동");
        influencerUserSearchRequestDTO3.setSearchType("");
        List<InfluencerUserDTO> influencerUserDTOList3 = influencerUserMapper.searchInfluencerUser(influencerUserSearchRequestDTO3);
        log.info("searchType 비었을 경우  : {} , searchType : {} , keyword : {}", influencerUserDTOList3.size(), influencerUserSearchRequestDTO3.getSearchType(), influencerUserSearchRequestDTO3.getKeyword());

        // 인플루언서 ID 검색
        InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO4 = new InfluencerUserSearchRequestDTO();
        influencerUserSearchRequestDTO4.setKeyword("INF0004");
        influencerUserSearchRequestDTO4.setSearchType("userId");
        List<InfluencerUserDTO> influencerUserDTOList4 = influencerUserMapper.searchInfluencerUser(influencerUserSearchRequestDTO4);
        log.info("searchType 비었을 경우  : {} , searchType : {} , keyword : {}", influencerUserDTOList4.size(), influencerUserSearchRequestDTO4.getSearchType(), influencerUserSearchRequestDTO4.getKeyword());

        // 인플루언서 이름 검색
        InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO5 = new InfluencerUserSearchRequestDTO();
        influencerUserSearchRequestDTO5.setKeyword("사용자124");
        influencerUserSearchRequestDTO5.setSearchType("name");
        List<InfluencerUserDTO> influencerUserDTOList5 = influencerUserMapper.searchInfluencerUser(influencerUserSearchRequestDTO5);
        log.info("searchType 비었을 경우  : {} , searchType : {} , keyword : {}", influencerUserDTOList5.size(), influencerUserSearchRequestDTO5.getSearchType(), influencerUserSearchRequestDTO5.getKeyword());

        // 인플루언서 이메일 검색
        InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO6 = new InfluencerUserSearchRequestDTO();
        influencerUserSearchRequestDTO6.setKeyword("user2@example.com");
        influencerUserSearchRequestDTO6.setSearchType("email");
        List<InfluencerUserDTO> influencerUserDTOList6 = influencerUserMapper.searchInfluencerUser(influencerUserSearchRequestDTO6);
        log.info("searchType 비었을 경우  : {} , searchType : {} , keyword : {}", influencerUserDTOList6.size(), influencerUserSearchRequestDTO6.getSearchType(), influencerUserSearchRequestDTO6.getKeyword());

        // 인플루언서 연락처 검색
        InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO7 = new InfluencerUserSearchRequestDTO();
        influencerUserSearchRequestDTO7.setKeyword("010-7062-0289");
        influencerUserSearchRequestDTO7.setSearchType("phone");
        List<InfluencerUserDTO> influencerUserDTOList7 = influencerUserMapper.searchInfluencerUser(influencerUserSearchRequestDTO7);
        log.info("searchType 비었을 경우  : {} , searchType : {} , keyword : {}", influencerUserDTOList7.size(), influencerUserSearchRequestDTO7.getSearchType(), influencerUserSearchRequestDTO7.getKeyword());

        // SNS 채널명 검색
        InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO8 = new InfluencerUserSearchRequestDTO();
        influencerUserSearchRequestDTO8.setKeyword("asdas");
        influencerUserSearchRequestDTO8.setSearchType("snsChannelName");
        List<InfluencerUserDTO> influencerUserDTOList8 = influencerUserMapper.searchInfluencerUser(influencerUserSearchRequestDTO8);
        log.info("searchType 비었을 경우  : {} , searchType : {} , keyword : {}", influencerUserDTOList8.size(), influencerUserSearchRequestDTO8.getSearchType(), influencerUserSearchRequestDTO8.getKeyword());

        // SNS 링크 검색
        InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO9 = new InfluencerUserSearchRequestDTO();
        influencerUserSearchRequestDTO9.setKeyword("asdaasd");
        influencerUserSearchRequestDTO9.setSearchType("snsLink");
        List<InfluencerUserDTO> influencerUserDTOList9 = influencerUserMapper.searchInfluencerUser(influencerUserSearchRequestDTO9);
        log.info("searchType 비었을 경우  : {} , searchType : {} , keyword : {}", influencerUserDTOList9.size(), influencerUserSearchRequestDTO9.getSearchType(), influencerUserSearchRequestDTO9.getKeyword());


    }

}

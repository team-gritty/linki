package com.linki.admin_integration_service.domain.user.repository.myBatis;


import com.linki.admin_integration_service.domain.user.dto.InfluencerUserDTO;
import com.linki.admin_integration_service.domain.user.dto.InfluencerUserSearchRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface InfluencerUserMapper {
    List<InfluencerUserDTO> getAllInfluencerUsers();
    List<InfluencerUserDTO> searchInfluencerUser(InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO);
    
    // Keyset 페이지네이션 메서드들
    List<InfluencerUserDTO> getAllInfluencerUsersWithKeyset(@Param("cursor") String cursor, @Param("size") int size);
    List<InfluencerUserDTO> searchInfluencerUserWithKeyset(@Param("searchDTO") InfluencerUserSearchRequestDTO searchDTO, @Param("cursor") String cursor, @Param("size") int size);
    
    // 채널 정보 별도 조회
    List<Map<String, Object>> getChannelsByInfluencerIds(@Param("influencerIds") List<String> influencerIds);
}

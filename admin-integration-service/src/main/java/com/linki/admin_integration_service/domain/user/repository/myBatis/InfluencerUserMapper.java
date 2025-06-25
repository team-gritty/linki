package com.linki.admin_integration_service.domain.user.repository.myBatis;


import com.linki.admin_integration_service.domain.user.dto.InfluencerUserDTO;
import com.linki.admin_integration_service.domain.user.dto.InfluencerUserSearchRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InfluencerUserMapper {
    List<InfluencerUserDTO> getAllInfluencerUsers();
    List<InfluencerUserDTO> searchInfluencerUser(InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO);
    
    // Keyset 페이지네이션 메서드들
    List<InfluencerUserDTO> getAllInfluencerUsersWithKeyset(String cursor, int size);
    List<InfluencerUserDTO> searchInfluencerUserWithKeyset(InfluencerUserSearchRequestDTO searchDTO, String cursor, int size);
}

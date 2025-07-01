package com.linki.admin_integration_service.domain.user.service;

import com.linki.admin_integration_service.domain.user.dto.InfluencerUserDTO;
import com.linki.admin_integration_service.domain.user.dto.InfluencerUserSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.dto.InfluencerKeysetResponseDTO;

import java.util.List;

public interface InfluencerUserService {
    List<InfluencerUserDTO> getAllInfluencerUsers();
    List<InfluencerUserDTO> searchInfluencerUser(InfluencerUserSearchRequestDTO influencerUserSearchRequestDTO);
    String exportExcel();
    
    // Keyset 페이지네이션 메서드들
    InfluencerKeysetResponseDTO getAllInfluencerUsersWithKeyset(String cursor, int size);
    InfluencerKeysetResponseDTO searchInfluencerUserWithKeyset(InfluencerUserSearchRequestDTO searchDTO, String cursor, int size);
}

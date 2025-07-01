package com.linki.admin_integration_service.domain.user.service;

import com.linki.admin_integration_service.domain.user.dto.SubscriberSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.dto.SubscriberUserDTO;
import com.linki.admin_integration_service.domain.user.dto.SubscriberKeysetResponseDTO;

import java.util.List;

public interface SubscriberUserService {
    List<SubscriberUserDTO> getAllSubscriberUsers();
    List<SubscriberUserDTO> searchSubscriberUser(SubscriberSearchRequestDTO subscriberSearchRequestDTO);
    String exportExcel();
    
    // Keyset 페이지네이션 메서드들
    SubscriberKeysetResponseDTO getAllSubscriberUsersWithKeyset(String cursor, int size);
    SubscriberKeysetResponseDTO searchSubscriberUserWithKeyset(SubscriberSearchRequestDTO searchDTO, String cursor, int size);
}

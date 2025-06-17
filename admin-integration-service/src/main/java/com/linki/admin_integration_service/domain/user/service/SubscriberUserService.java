package com.linki.admin_integration_service.domain.user.service;

import com.linki.admin_integration_service.domain.user.dto.SubscriberSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.dto.SubscriberUserDTO;


import java.util.List;

public interface SubscriberUserService {
    List<SubscriberUserDTO> getAllSubscriberUsers();
    List<SubscriberUserDTO> searchSubscriberUser(SubscriberSearchRequestDTO subscriberSearchRequestDTO);
}

package com.linki.admin_integration_service.domain.user.service;

import com.linki.admin_integration_service.domain.user.dto.AdvertiserSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.dto.AdvertiserUserDTO;

import java.util.List;

public interface AdvertiserUserService {
    List<AdvertiserUserDTO> getAllAdvertiserUsers();
    List<AdvertiserUserDTO> searchAdvertiserUser(AdvertiserSearchRequestDTO advertiserSearchRequestDTO);
}

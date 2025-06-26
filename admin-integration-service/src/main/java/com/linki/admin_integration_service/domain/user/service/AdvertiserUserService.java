package com.linki.admin_integration_service.domain.user.service;

import com.linki.admin_integration_service.domain.user.dto.*;

import java.util.List;

public interface AdvertiserUserService {
    List<AdvertiserUserDTO> getAllAdvertiserUsers();
    List<AdvertiserUserDTO> searchAdvertiserUser(AdvertiserSearchRequestDTO advertiserSearchRequestDTO);
    String exportExcel();
    AdvertiserKeysetResponseDTO getAllAdvertiserUsersWithKeyset(String cursor, int size);
    AdvertiserKeysetResponseDTO searchAdvertiserUserWithKeyset(AdvertiserSearchRequestDTO searchDTO, String cursor, int size);
}
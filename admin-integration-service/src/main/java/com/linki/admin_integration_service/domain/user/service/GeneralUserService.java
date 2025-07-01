package com.linki.admin_integration_service.domain.user.service;

import com.linki.admin_integration_service.domain.user.dto.GeneralUSerDTO;
import com.linki.admin_integration_service.domain.user.dto.GeneralUserKeysetResponseDTO;
import com.linki.admin_integration_service.domain.user.dto.GeneralUserSearchRequestDTO;

import java.util.List;

public interface GeneralUserService {
    List<GeneralUSerDTO> getAllGeneralUsers();
    List<GeneralUSerDTO> searchGeneralUser(GeneralUserSearchRequestDTO generalUserSearchRequestDTO);
    String exportExcel();


    GeneralUserKeysetResponseDTO getAllGeneralUsersWithKeyset(String cursor, int size);
    GeneralUserKeysetResponseDTO searchGeneralUserWithKeyset(GeneralUserSearchRequestDTO searchDTO, String cursor, int size);
}



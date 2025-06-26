package com.linki.admin_integration_service.domain.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.*;

import java.util.List;

public interface AdminSignUpAgreementService {
    List<AdminSignUpAgreementDTO> getAdminSignUpList();
    Boolean approveAdminSignUp(AdminSignUpAgreementRequestDTO adminSignUpAgreementRequestDTO);
    Boolean rejectAdminSignUp(AdminSignUpAgreementRequestDTO adminSignUpAgreementRequestDTO);
    List<AdminSignUpAgreementDTO> searchAdminSignUp(AdminSignUpAgreementSearchRequestDTO adminSignUpAgreementSearchRequestDTO);
    String exportExcel();

    AdminSignUpKeysetResponseDTO getAdminSignUpListWithKeyset(String cursor, int size);
    AdminSignUpKeysetResponseDTO searchAdminSignUpWithKeyset(AdminSignUpAgreementSearchRequestDTO adminSignUpAgreementSearchRequestDTO, String cursor, int size);
}

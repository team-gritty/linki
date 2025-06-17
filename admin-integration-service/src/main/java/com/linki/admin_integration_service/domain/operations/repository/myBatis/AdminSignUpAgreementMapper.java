package com.linki.admin_integration_service.domain.operations.repository.myBatis;

import com.linki.admin_integration_service.domain.operations.dto.AdminSignUpAgreementDTO;
import com.linki.admin_integration_service.domain.operations.dto.AdminSignUpAgreementSearchRequestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminSignUpAgreementMapper {
    List<AdminSignUpAgreementDTO> getAdminSignUpList();
    List<AdminSignUpAgreementDTO> searchAdminSignUp(AdminSignUpAgreementSearchRequestDTO adminSignUpAgreementSearchRequestDTO);
}

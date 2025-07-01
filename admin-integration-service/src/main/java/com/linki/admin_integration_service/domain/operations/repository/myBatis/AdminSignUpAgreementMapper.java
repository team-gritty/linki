package com.linki.admin_integration_service.domain.operations.repository.myBatis;

import com.linki.admin_integration_service.domain.operations.dto.AdminSignUpAgreementDTO;
import com.linki.admin_integration_service.domain.operations.dto.AdminSignUpAgreementSearchRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminSignUpAgreementMapper {
    List<AdminSignUpAgreementDTO> getAdminSignUpList();
    List<AdminSignUpAgreementDTO> searchAdminSignUp(AdminSignUpAgreementSearchRequestDTO adminSignUpAgreementSearchRequestDTO);

    List<AdminSignUpAgreementDTO> getAdminSignUpListWithKeyset(@Param("cursor") String cursor, @Param("size") int size);
    List<AdminSignUpAgreementDTO> searchAdminSignUpWithKeyset(@Param("searchDTO") AdminSignUpAgreementSearchRequestDTO adminSignUpAgreementSearchRequestDTO, @Param("cursor") String cursor, @Param("size") int size);
}

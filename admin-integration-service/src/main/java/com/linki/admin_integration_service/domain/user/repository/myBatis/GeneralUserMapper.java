package com.linki.admin_integration_service.domain.user.repository.myBatis;

import com.linki.admin_integration_service.domain.user.dto.GeneralUSerDTO;
import com.linki.admin_integration_service.domain.user.dto.GeneralUserSearchRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GeneralUserMapper {
    List<GeneralUSerDTO> getAllGeneralUsers();
    List<GeneralUSerDTO> searchGeneralUser(GeneralUserSearchRequestDTO generalUserSearchRequestDTO);


    List<GeneralUSerDTO> getAllGeneralUsersWithKeyset(@Param("cursor") String cursor, @Param("size") int size);
    List<GeneralUSerDTO> searchGeneralUserWithKeyset(@Param("searchDTO") GeneralUserSearchRequestDTO searchDTO, @Param("cursor") String cursor, @Param("size") int size);
}


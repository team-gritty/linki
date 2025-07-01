package com.linki.admin_integration_service.domain.user.repository.myBatis;

import com.linki.admin_integration_service.domain.user.dto.AdvertiserSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.dto.AdvertiserUserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdvertiserUserMapper {
    List<AdvertiserUserDTO> getAllAdvertiserUsers();
    List<AdvertiserUserDTO> searchAdvertiserUser(AdvertiserSearchRequestDTO advertiserSearchRequestDTO);

    List<AdvertiserUserDTO> getAllAdvertiserUsersWithKeyset(@Param("cursor") String cursor, @Param("size") int size);
    List<AdvertiserUserDTO> searchAdvertiserUserWithKeyset(@Param("searchDTO") AdvertiserSearchRequestDTO searchDTO, @Param("cursor") String cursor, @Param("size") int size);
}

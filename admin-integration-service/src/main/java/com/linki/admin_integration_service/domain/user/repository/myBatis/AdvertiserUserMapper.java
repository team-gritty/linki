package com.linki.admin_integration_service.domain.user.repository.myBatis;

import com.linki.admin_integration_service.domain.user.dto.AdvertiserSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.dto.AdvertiserUserDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdvertiserUserMapper {
    List<AdvertiserUserDTO> getAllAdvertiserUsers();
    List<AdvertiserUserDTO> searchAdvertiserUser(AdvertiserSearchRequestDTO advertiserSearchRequestDTO);
}

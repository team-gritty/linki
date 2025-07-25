package com.linki.admin_integration_service.domain.user.repository.myBatis;

import com.linki.admin_integration_service.domain.user.dto.SubscriberSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.dto.SubscriberUserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubscriberUserMapper {
    List<SubscriberUserDTO> getAllSubscriberUsers();
    List<SubscriberUserDTO> searchSubscriberUser(SubscriberSearchRequestDTO subscriberSearchRequestDTO);
    
    // Keyset 페이지네이션 메서드들
    List<SubscriberUserDTO> getAllSubscriberUsersWithKeyset(@Param("cursor") String cursor, @Param("size") int size);
    List<SubscriberUserDTO> searchSubscriberUserWithKeyset(@Param("searchDTO") SubscriberSearchRequestDTO searchDTO, @Param("cursor") String cursor, @Param("size") int size);
}

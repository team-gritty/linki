package com.linki.admin_integration_service.domain.payment.repository.myBatis;

import com.linki.admin_integration_service.domain.payment.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SettlementMapper {
    List<SettlementDTO> getAllSettlements();
    List<SettlementDTO> searchSettlement(SettlementSearchDTO settlementSearchDTO);

    List<SettlementDTO> getAllSettlementsWithKeyset(@Param("cursor") String cursor, @Param("size") int size);
    List<SettlementDTO> searchSettlementWithKeyset(@Param("searchDTO") SettlementSearchDTO searchDTO, @Param("cursor") String cursor, @Param("size") int size);
}


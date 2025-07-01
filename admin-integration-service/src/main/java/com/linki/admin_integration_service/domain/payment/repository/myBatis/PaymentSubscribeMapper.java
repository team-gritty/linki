package com.linki.admin_integration_service.domain.payment.repository.myBatis;

import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeDTO;
import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeSearchDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentSubscribeMapper {
    List<PaymentSubscribeDTO> getAllPaymentSubscribes();
    List<PaymentSubscribeDTO> searchPaymentSubscribe(PaymentSubscribeSearchDTO searchDTO);


    List<PaymentSubscribeDTO> getAllPaymentSubscribesWithKeyset(@Param("cursor") String cursor, @Param("size") int size);
    List<PaymentSubscribeDTO> searchPaymentSubscribeWithKeyset(@Param("searchDTO") PaymentSubscribeSearchDTO searchDTO, @Param("cursor") String cursor, @Param("size") int size);
}


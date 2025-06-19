package com.linki.admin_integration_service.domain.payment.repository.myBatis;

import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeDTO;
import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeSearchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentSubscribeMapper {
    List<PaymentSubscribeDTO> getAllPaymentSubscribes();
    List<PaymentSubscribeDTO> searchPaymentSubscribe(PaymentSubscribeSearchDTO searchDTO);
}

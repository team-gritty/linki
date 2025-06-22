package com.linki.admin_integration_service.domain.payment.dto;

import com.linki.admin_integration_service.util.excel.ExcelAnnotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ExcelAnnotation.ExcelSheet(name = "SubscribePaymentList")
public class PaymentSubscribeDTO {
    @ExcelAnnotation.ExcelColumn(headerName = "회원 구분")
    private String memberType;

    @ExcelAnnotation.ExcelColumn(headerName = "이름")
    private String name;

    @ExcelAnnotation.ExcelColumn(headerName = "로그인 ID")
    private String loginId;

    @ExcelAnnotation.ExcelColumn(headerName = "연락처")
    private String phone;

    @ExcelAnnotation.ExcelColumn(headerName = "구독 시작일")
    private LocalDateTime subscriptionStartDate;

    @ExcelAnnotation.ExcelColumn(headerName = "구독 마감일")
    private LocalDateTime subscriptionEndDate;

    @ExcelAnnotation.ExcelColumn(headerName = "이전 결제일")
    private LocalDateTime previousPaymentDate;

    @ExcelAnnotation.ExcelColumn(headerName = "다음 결제일")
    private LocalDateTime nextPaymentDate;
}

package com.linki.admin_integration_service.domain.payment.dto;

import com.linki.admin_integration_service.util.excel.ExcelAnnotation;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ExcelAnnotation.ExcelSheet(name = "SettlementList")
public class SettlementDTO {

    @ExcelAnnotation.ExcelColumn(headerName = "계약 ID")
    private String contractId;

    @ExcelAnnotation.ExcelColumn(headerName = "광고주")
    private String advertiserName;

    @ExcelAnnotation.ExcelColumn(headerName = "인플루언서")
    private String influencerName;

    @ExcelAnnotation.ExcelColumn(headerName = "광고 시작일")
    private LocalDate adStartDate;

    @ExcelAnnotation.ExcelColumn(headerName = "광고 마감일")
    private LocalDate adEndDate;

    @ExcelAnnotation.ExcelColumn(headerName = "광고 금액")
    private BigDecimal adAmount;

    @ExcelAnnotation.ExcelColumn(headerName = "정산 여부")
    private String isSettled;
}

package com.linki.admin_integration_service.domain.contract.dto;

import com.linki.admin_integration_service.util.excel.ExcelAnnotation;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ExcelAnnotation.ExcelSheet(name = "contractList")
public class ContractDTO {

    @ExcelAnnotation.ExcelColumn(headerName = "계약 ID")
    private String contractId;

    @ExcelAnnotation.ExcelColumn(headerName = "광고 시작일")
    private LocalDate adStartDate;

    @ExcelAnnotation.ExcelColumn(headerName = "광고 마감일")
    private LocalDate adEndDate;

    @ExcelAnnotation.ExcelColumn(headerName = "광고 계약 금액")
    private BigDecimal contractAmount;

    @ExcelAnnotation.ExcelColumn(headerName = "광고비 결제일")
    private LocalDate paymentDate;

    @ExcelAnnotation.ExcelColumn(headerName = "인플루언서")
    private String influencerName;

    @ExcelAnnotation.ExcelColumn(headerName = "광고주")
    private String advertiserName;

    @ExcelAnnotation.ExcelColumn(headerName = "계약 상태")
    private String contractStatus;

    @ExcelAnnotation.ExcelColumn(headerName = "계약서 Link")
    private String contractLink;
}

package com.linki.admin_integration_service.domain.contract.dto;

import com.linki.admin_integration_service.util.excel.ExcelAnnotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
@ExcelAnnotation.ExcelSheet(name = "CampaignList")
public class CampaignDTO {

    @ExcelAnnotation.ExcelColumn(headerName = "캠페인 ID")
    private String campaignId;

    @ExcelAnnotation.ExcelColumn(headerName = "광고주")
    private String advertiserName;

    @ExcelAnnotation.ExcelColumn(headerName = "사업자 번호")
    private String businessNumber;

    @ExcelAnnotation.ExcelColumn(headerName = "연락처")
    private String phone;

    @ExcelAnnotation.ExcelColumn(headerName = "캠페인 제목")
    private String campaignTitle;

    @ExcelAnnotation.ExcelColumn(headerName = "캠페인 등록일")
    private LocalDate registerDate;

    @ExcelAnnotation.ExcelColumn(headerName = "캠페인 지원 마감일")
    private LocalDate applyDeadline;

    @ExcelAnnotation.ExcelColumn(headerName = "캠페인 Link")
    private String campaignLink;
}

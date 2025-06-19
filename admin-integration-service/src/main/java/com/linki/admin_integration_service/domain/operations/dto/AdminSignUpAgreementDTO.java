package com.linki.admin_integration_service.domain.operations.dto;

import com.linki.admin_integration_service.util.excel.ExcelAnnotation;
import lombok.Data;

@Data
@ExcelAnnotation.ExcelSheet(name = "AdminList")
public class AdminSignUpAgreementDTO {
    @ExcelAnnotation.ExcelColumn(headerName = "관리자 로그인 ID")
    private String	adminSignUpId;

    @ExcelAnnotation.ExcelColumn(headerName = "이름")
    private String	adminName;

    @ExcelAnnotation.ExcelColumn(headerName = "이메일")
    private String	adminEmail;

    @ExcelAnnotation.ExcelColumn(headerName = "연락처")
    private String	adminPhone;

    @ExcelAnnotation.ExcelColumn(headerName = "가입상태")
    private String 	adminStatus;
}

package com.linki.admin_integration_service.domain.user.dto;

import com.linki.admin_integration_service.util.excel.ExcelAnnotation;
import lombok.Data;

@Data
@ExcelAnnotation.ExcelSheet(name = "AdvertiserUserList")
public class AdvertiserUserDTO {
    @ExcelAnnotation.ExcelColumn(headerName = "User ID")
    private String userId;

    @ExcelAnnotation.ExcelColumn(headerName = "사업자 번호")
    private String businessNumber;

    @ExcelAnnotation.ExcelColumn(headerName = "사업자 명")
    private String businessName;

    @ExcelAnnotation.ExcelColumn(headerName = "이름")
    private String name;

    @ExcelAnnotation.ExcelColumn(headerName = "연락처")
    private String phone;

    @ExcelAnnotation.ExcelColumn(headerName = "이메일")
    private String email;

    @ExcelAnnotation.ExcelColumn(headerName = "userCursor")
    private String userCursor;
}

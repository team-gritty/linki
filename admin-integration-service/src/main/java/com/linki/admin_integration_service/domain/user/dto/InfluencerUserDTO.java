package com.linki.admin_integration_service.domain.user.dto;

import com.linki.admin_integration_service.util.excel.ExcelAnnotation;
import lombok.Data;

@Data
@ExcelAnnotation.ExcelSheet(name = "InfluencerUserList")
public class InfluencerUserDTO {

    @ExcelAnnotation.ExcelColumn(headerName = "User Id")
    private String userId;

    @ExcelAnnotation.ExcelColumn(headerName = "이름")
    private String name;

    @ExcelAnnotation.ExcelColumn(headerName = "이메일")
    private String email;

    @ExcelAnnotation.ExcelColumn(headerName = "연락처")
    private String phone;

    @ExcelAnnotation.ExcelColumn(headerName = "SNS 이름")
    private String snsChannelName;

    @ExcelAnnotation.ExcelColumn(headerName = "SNS Link")
    private String snsLink;

    @ExcelAnnotation.ExcelColumn(headerName = "userCursor")
    private String userCursor;
}

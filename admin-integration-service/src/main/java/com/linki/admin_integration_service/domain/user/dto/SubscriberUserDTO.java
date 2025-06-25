package com.linki.admin_integration_service.domain.user.dto;

import com.linki.admin_integration_service.util.excel.ExcelAnnotation;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ExcelAnnotation.ExcelSheet(name = "SubscriberUserList")
public class SubscriberUserDTO {
    @ExcelAnnotation.ExcelColumn(headerName = "User ID")
    private String userId;

    @ExcelAnnotation.ExcelColumn(headerName = "이름")
    private String name;

    @ExcelAnnotation.ExcelColumn(headerName = "이메일")
    private String email;

    @ExcelAnnotation.ExcelColumn(headerName = "연락처")
    private String phone;

    @ExcelAnnotation.ExcelColumn(headerName = "구독 시작일")
    private LocalDateTime subscriptionStartDate;

    @ExcelAnnotation.ExcelColumn(headerName = "구독 종료알")
    private LocalDateTime subscriptionEndDate;

    @ExcelAnnotation.ExcelColumn(headerName = "userCursor")
    private String userCursor;
}

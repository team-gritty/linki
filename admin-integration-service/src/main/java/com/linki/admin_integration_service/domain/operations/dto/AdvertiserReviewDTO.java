package com.linki.admin_integration_service.domain.operations.dto;

import com.linki.admin_integration_service.util.excel.ExcelAnnotation;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelAnnotation.ExcelSheet(name = "AdvertiserReviewsList")
public class AdvertiserReviewDTO {

    @ExcelAnnotation.ExcelColumn(headerName = "리뷰 ID")
    private String	advertiserReviewId;

    @ExcelAnnotation.ExcelColumn(headerName = "광고주")
    private String advertiser;

    @ExcelAnnotation.ExcelColumn(headerName = "작성자")
    private String writer;

    @ExcelAnnotation.ExcelColumn(headerName = "리뷰")
    private String review;

    @ExcelAnnotation.ExcelColumn(headerName = "평점")
    private BigDecimal rating;

    @ExcelAnnotation.ExcelColumn(headerName = "리뷰 작성일")
    private LocalDateTime reviewDate;

    @ExcelAnnotation.ExcelColumn(headerName = "공개 여부")
    private boolean visibility;

    @ExcelAnnotation.ExcelColumn(headerName = "계약 ID")
    private String contractId;
}

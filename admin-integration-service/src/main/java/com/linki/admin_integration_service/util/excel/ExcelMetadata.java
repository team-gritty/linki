package com.linki.admin_integration_service.util.excel;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ExcelMetadata {

    private final Map<String,String> excelHeaderNames;
    private final List<String> detaFieldNames;
    private final String sheetName;

    public ExcelMetadata(Map<String, String> excelHeaderNames, List<String> detaFieldNames, String sheetName) {
        this.excelHeaderNames = excelHeaderNames;
        this.detaFieldNames = detaFieldNames;
        this.sheetName = sheetName;
    }


    public String getHeaderName(String fieldName) {
        return excelHeaderNames.getOrDefault(fieldName,"");
    }




}

package com.linki.admin_integration_service.util.excel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ExcelSheetData {
    private final List<?> dataList;
    private final Class<?> type;

    public static ExcelSheetData of(List<?> dataList, Class<?> type) {
        return new ExcelSheetData(dataList, type);
    }
}

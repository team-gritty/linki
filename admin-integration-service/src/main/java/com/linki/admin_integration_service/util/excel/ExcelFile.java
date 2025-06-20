package com.linki.admin_integration_service.util.excel;

import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;

public interface ExcelFile {

    void write(OutputStream outputStream) throws IOException;
    void writeWithEncryption(OutputStream outputStream,String password) throws IOException;

    default <T> void createCell(Row row, int column, T value, CellStyle style){

        if(value == null){
            return;
        }

        Cell cell = row.createCell(column);
        if(value instanceof Integer){
            cell.setCellValue((Integer) value);
        }
        else if(value instanceof Long){
            cell.setCellValue((Long) value);
        }
        else if(value instanceof Boolean){
            cell.setCellValue((Boolean) value);
        }
        else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof java.time.LocalDate) {
            cell.setCellValue(((java.time.LocalDate) value).toString());
        } else {
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);

    }

    default CellStyle createCellStyle(Workbook wb,boolean isBold){
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(isBold);
        style.setFont(font);
        return style;

    }
}

package com.linki.admin_integration_service.util.excel;

import io.micrometer.common.lang.Nullable;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class SXSSFExcelFile extends BaseSXSSFExcelFile {


    public SXSSFExcelFile(ExcelSheetData data, HttpServletResponse response, @Nullable ExcelMetadata metadata) {
        super(data, response, metadata);
    }

    public SXSSFExcelFile(){}

    public SXSSFExcelFile(ExcelSheetData data,HttpServletResponse response) throws IOException {
        this(data,response,(ExcelMetadata) null);
    }

    public SXSSFExcelFile(ExcelSheetData data,HttpServletResponse response,
                          @Nullable String password) throws IOException {
    ExcelMetadata metadata = ExcelMetadataFactory.getInstance().createMetadata(data.getType());
        exportExcelFile(data, metadata, response.getOutputStream(), password);

    }

    public ByteArrayOutputStream generateExcelToStream(ExcelSheetData data, @Nullable String password) throws IOException {
        ExcelMetadata metadata = ExcelMetadataFactory.getInstance().createMetadata(data.getType());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderHeaders(metadata);
        renderDataLines(data);
        writeWithEncryption(out, password);
        return out;
    }

    private void exportExcelFile(ExcelSheetData data,ExcelMetadata metadata,ServletOutputStream stream,String password) throws IOException {

        renderHeaders(metadata);
        renderDataLines(data);
        writeWithEncryption(stream, password);

    }


}

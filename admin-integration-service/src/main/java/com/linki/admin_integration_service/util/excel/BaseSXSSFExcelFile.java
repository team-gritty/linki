package com.linki.admin_integration_service.util.excel;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.util.List;

import static com.linki.admin_integration_service.util.excel.SuperClassReflectionUtils.getAllFields;

public abstract class BaseSXSSFExcelFile implements ExcelFile {

    protected static final int ROW_ACCESS_WINDOW_SIZE = 1000;
    protected static final int ROW_START_INDEX = 0;
    protected static final int COLUMN_START_INDEX = 0;

    protected SXSSFWorkbook workbook;
    protected Sheet sheet;

    public BaseSXSSFExcelFile() {
        this.workbook = new SXSSFWorkbook(ROW_ACCESS_WINDOW_SIZE);
    }

    public BaseSXSSFExcelFile(ExcelSheetData data, HttpServletResponse response, ExcelMetadata metadata) {
    }

    protected void renderHeaders(ExcelMetadata excelMetadata) {
        sheet = workbook.createSheet(excelMetadata.getSheetName());
        Row row = sheet.createRow(ROW_START_INDEX);
        int columnIndex = COLUMN_START_INDEX;
        CellStyle style = createCellStyle(workbook,true);

        for (String fieldName :excelMetadata.getDetaFieldNames()){
            createCell(row, columnIndex++, excelMetadata.getHeaderName(fieldName), style);
        }
    }


    protected void renderDataLines(ExcelSheetData excelSheetData) {
        CellStyle style = createCellStyle(workbook,false);
        int rowIndex = ROW_START_INDEX + 1;
        List<Field> fields = getAllFields(excelSheetData.getType());
        for(Object record : excelSheetData.getDataList()){
            Row row = sheet.createRow(rowIndex++);
            int columnIndex = COLUMN_START_INDEX;

            try {
                for (Field field : fields) {
                    field.setAccessible(true);
                    createCell(row, columnIndex++, field.get(record), style);
                }
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Error while creating data line", e);
            }
        }
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        workbook.write(outputStream);
    }

    @Override
    public void writeWithEncryption(OutputStream outputStream, String password) throws IOException {
        if (password == null) {
            write(outputStream);
        }else {
            POIFSFileSystem fileSystem = new POIFSFileSystem();
            OutputStream encryptorStream = getEncryptoStream(fileSystem,password);
            workbook.write(encryptorStream);
            encryptorStream.close();

            fileSystem.writeFilesystem(encryptorStream);
            fileSystem.close();
        }
        workbook.close();
        outputStream.close();
    }

    private OutputStream getEncryptoStream(POIFSFileSystem fileSystem,String password) throws IOException {

        try {
            Encryptor encryptor = new EncryptionInfo(EncryptionMode.agile).getEncryptor();
            encryptor.confirmPassword(password);
            return encryptor.getDataStream(fileSystem);
        }catch (IOException | GeneralSecurityException e){
            throw new RuntimeException("Failed to encrypt data", e);
        }


    }
}

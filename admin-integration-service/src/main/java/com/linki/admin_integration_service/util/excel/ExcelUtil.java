package com.linki.admin_integration_service.util.excel;

import com.linki.admin_integration_service.util.ObjectStorage;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelUtil {

    private final ObjectStorage objectStorage;

    public <T> String exportExcel(List<T> list, Class<?> clazz, String fileName, @Nullable String password){
        try {
            // 1. 파일 이름 조립
            if (!fileName.endsWith(".xlsx")) {
                fileName += ".xlsx";
            }

            // 2. ExcelSheetData로 변환 (예시용 더미)
            ExcelSheetData data = new ExcelSheetData(list, clazz);

            // 3. Excel 생성기 준비
            SXSSFExcelFile excelFile = new SXSSFExcelFile();

            // 4. 엑셀을 메모리에 생성
            ByteArrayOutputStream baos = excelFile.generateExcelToStream(data, password);

            // 5. 오브젝트 스토리지 업로드
            MultipartFile file = new MockMultipartFile(
                    "file",                             // name
                    fileName,            // original filename
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // content type
                    baos.toByteArray()                 // content
            );
            String result = objectStorage.uploadFile(file);

            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
}

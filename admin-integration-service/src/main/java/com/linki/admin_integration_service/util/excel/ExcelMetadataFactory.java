package com.linki.admin_integration_service.util.excel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.reflect.FieldUtils.getAllFields;

public class ExcelMetadataFactory {
    private ExcelMetadataFactory() {
    }

    private static class SingletonHolder {
        private static final ExcelMetadataFactory INSTANCE = new ExcelMetadataFactory();
    }

    public static ExcelMetadataFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ExcelMetadata createMetadata(Class<?> clazz) {
        Map<String, String> headerNamesMap = new LinkedHashMap<>();
        List<String> dataFieldNameList = new ArrayList<>();

        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(ExcelAnnotation.ExcelColumn.class)) {

                ExcelAnnotation.ExcelColumn columnAnnotation = field.getAnnotation(ExcelAnnotation.ExcelColumn.class);
                headerNamesMap.put(field.getName(), columnAnnotation.headerName());
                dataFieldNameList.add(field.getName());
            }
        }
        if(headerNamesMap.isEmpty()){
            throw new RuntimeException(String.format("No Excel header found in class [%s]",clazz.getName()));
        }

        return new ExcelMetadata(headerNamesMap,dataFieldNameList, getSheetName(clazz));

    }

    private String getSheetName(Class<?> clazz) {
        ExcelAnnotation.ExcelSheet excelSheetAnnotation = clazz.getAnnotation(ExcelAnnotation.ExcelSheet.class);
        if (excelSheetAnnotation != null) {
            return excelSheetAnnotation.name();
        }
        return clazz.getSimpleName();
    }

}

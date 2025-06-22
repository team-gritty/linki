package com.linki.admin_integration_service.util.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ExcelAnnotation {


    /**
     * 사용 방법 :
     * @ExcelColumn(headerName = "이름")
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ExcelColumn {
        String headerName() default "";
    }

    /**
     * 사용 방법 :
     * @ExcelSheet(name = "Users")
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ExcelSheet {
        String name() default "";
    }





}

package com.linki.admin_integration_service.util.excel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SuperClassReflectionUtils {

    private SuperClassReflectionUtils() {}

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for(Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    public static Annotation getAnnotation(Class<?> clazz, 
                                           Class<? extends Annotation> targetAnnotation) {
        for(Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            if(c.isAnnotationPresent(targetAnnotation)) {
                return c.getAnnotation(targetAnnotation);
            }
        }
        return null;
    }

    public static Field getFields(Class<?> clazz,String name) throws Exception {
        for (Class<?> clazzInClasses : getAllClassesIncludingSuperClasses(clazz, false)) {
               for (Field field : clazzInClasses.getDeclaredFields()) {
                   if (field.getName().equals(name)) {
                       return clazzInClasses.getDeclaredField(name);
                   }
               }

        }
        throw new NoSuchFieldException("No such field: " + name);
    }

    private static List<Class<?>> getAllClassesIncludingSuperClasses(Class<?> clazz, boolean fromSuper) {
        List<Class<?>> classes = new ArrayList<>();
        while (clazz != null) {
            classes.add(clazz);
            clazz = clazz.getSuperclass();
        }
        if (fromSuper) {
            Collections.reverse(classes);
        }
        return classes;

    }

}

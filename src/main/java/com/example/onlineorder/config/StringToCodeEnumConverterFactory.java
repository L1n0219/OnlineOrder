package com.example.onlineorder.config;
import com.baomidou.mybatisplus.annotation.EnumValue;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.lang.reflect.Field;

public class StringToCodeEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

    @Override
    public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        return new CodeEnumConverter<>(targetType);
    }

    private static class CodeEnumConverter<T extends Enum<?>> implements Converter<String, T> {
        private final Class<T> enumType;

        CodeEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            int code = Integer.parseInt(source.trim());
            for (T constant : enumType.getEnumConstants()) {
                for (Field field : enumType.getDeclaredFields()) {
                    if (field.isAnnotationPresent(EnumValue.class)) {
                        try {
                            field.setAccessible(true);
                            if (field.getInt(constant) == code) {
                                return constant;
                            }
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            throw new IllegalArgumentException("未知的枚举 code：" + code);
        }
    }
}


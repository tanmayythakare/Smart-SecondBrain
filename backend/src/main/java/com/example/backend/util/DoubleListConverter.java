package com.example.backend.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class DoubleListConverter implements AttributeConverter<List<Double>, String> {

    @Override
    public String convertToDatabaseColumn(List<Double> attribute) {
        if (attribute == null || attribute.isEmpty()) return null;
        return attribute.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<Double> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        return Arrays.stream(dbData.split(","))
                .map(Double::valueOf)
                .collect(Collectors.toList());
    }
}

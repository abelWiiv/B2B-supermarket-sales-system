package com.supermarket.customermanagement.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Converter
public class EmailEncryptorConverter implements AttributeConverter<String, String> {

    @Autowired
    private TextEncryptor textEncryptor;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            return textEncryptor.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt email", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return textEncryptor.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt email", e);
        }
    }
}
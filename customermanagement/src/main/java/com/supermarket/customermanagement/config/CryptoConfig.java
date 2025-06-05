package com.supermarket.customermanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Configuration
public class CryptoConfig {

    @Value("${spring.encryption.password}")
    private String password;


    @Bean
    public TextEncryptor textEncryptor() {
//        String password = System.getProperty("spring.encryption.password"); // Matches application.yml
        String salt = "deadbeef"; // 8-byte hex-encoded salt
        return Encryptors.text(password, salt);
    }
}
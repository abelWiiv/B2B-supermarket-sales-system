//package com.supermarket.customermanagement.config;
//
//import org.jasypt.encryption.StringEncryptor;
//import org.jasypt.encryption.pbe.PBEStringEncryptor;
//import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
//import org.jasypt.hibernate5.encryptor.HibernatePBEStringEncryptor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class JasyptConfig {
//
//    @Bean(name = "jasyptStringEncryptor")
//    public StringEncryptor stringEncryptor() {
//        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//        encryptor.setProviderName("SunJCE");
//        encryptor.setPoolSize(2);
//        encryptor.setPassword(System.getenv("JASYPT_PASSWORD"));
//        encryptor.setAlgorithm("PBEWithMD5AndDES");
//        return encryptor;
//    }
//
//    @Bean(name = "hibernateStringEncryptor")
//    public HibernatePBEStringEncryptor hibernateStringEncryptor() {
//        HibernatePBEStringEncryptor encryptor = new HibernatePBEStringEncryptor();
//        encryptor.setRegisteredName("jasyptStringEncryptor");
//        encryptor.setEncryptor((PBEStringEncryptor) stringEncryptor());
//        return encryptor;
//    }
//}
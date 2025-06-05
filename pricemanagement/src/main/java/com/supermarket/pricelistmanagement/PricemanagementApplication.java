package com.supermarket.pricelistmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PricemanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(PricemanagementApplication.class, args);
	}

}

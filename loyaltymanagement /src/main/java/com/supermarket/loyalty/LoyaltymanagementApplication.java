package com.supermarket.loyalty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class LoyaltymanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoyaltymanagementApplication.class, args);

	}

}

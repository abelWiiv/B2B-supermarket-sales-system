package com.supermarket.pricelistmanagement.client;

import com.supermarket.pricelistmanagement.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "product-management", url = "${application.product-management.base-url}")
public interface ProductFeignClient {
    @GetMapping("/api/v1/products/{id}")
    ProductDto getProductById(@PathVariable("id") UUID id);
}



//package com.supermarket.pricelistmanagement.client;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.util.UUID;
//
//@FeignClient(name = "product-management", url = "${application.product-management.base-url}")
//public interface ProductFeignClient {
//    @GetMapping("/api/v1/products/{id}")
//    void getProductById(@PathVariable("id") UUID id);
//}
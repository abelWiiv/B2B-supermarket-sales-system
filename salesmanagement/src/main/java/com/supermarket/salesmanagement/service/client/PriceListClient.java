package com.supermarket.salesmanagement.service.client;

import com.supermarket.common.dto.PriceListResponse;
import com.supermarket.salesmanagement.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "price-list-management", url = "${application.price.service.url}", configuration = FeignClientConfig.class)
public interface PriceListClient {
    @GetMapping("/api/v1/price-lists/{productId}/prices")
    PriceListResponse getPriceByProductId(@PathVariable("productId") UUID productId);
}
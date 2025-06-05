package com.supermarket.pricelistmanagement.service;

import com.supermarket.pricelistmanagement.client.ProductFeignClient;
import com.supermarket.pricelistmanagement.dto.ProductDto;
import com.supermarket.pricelistmanagement.dto.request.PriceListCreateRequest;
import com.supermarket.pricelistmanagement.dto.request.PriceListUpdateRequest;
import com.supermarket.pricelistmanagement.dto.response.PriceListResponse;
import com.supermarket.pricelistmanagement.exception.CustomException;
import com.supermarket.pricelistmanagement.model.PriceList;
import com.supermarket.pricelistmanagement.repository.PriceListRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PriceListService {

    private final PriceListRepository priceListRepository;
    private final ProductFeignClient productFeignClient;

    public PriceListResponse createPriceList(PriceListCreateRequest request) {
        // Validate product ID
        validateProductId(request.getProductId());

        if (priceListRepository.existsByProductIdAndCustomerCategory(request.getProductId(), request.getCustomerCategory())) {
            throw new CustomException("Price list for product ID " + request.getProductId() +
                    " and customer category " + request.getCustomerCategory() + " already exists");
        }

        PriceList priceList = PriceList.builder()
                .productId(request.getProductId())
                .customerCategory(request.getCustomerCategory())
                .price(request.getPrice())
                .effectiveDate(request.getEffectiveDate())
                .build();

        PriceList savedPriceList = priceListRepository.save(priceList);
        return mapToPriceListResponse(savedPriceList);
    }

    public PriceListResponse getPriceListById(UUID id) {
        PriceList priceList = priceListRepository.findById(id)
                .orElseThrow(() -> new CustomException("Price list with ID " + id + " not found"));
        return mapToPriceListResponse(priceList);
    }

    public Page<PriceListResponse> getAllPriceLists(Pageable pageable) {
        return priceListRepository.findAll(pageable)
                .map(this::mapToPriceListResponse);
    }

    public ProductDto getProductById(UUID productId) {
        try {
            return productFeignClient.getProductById(productId);
        } catch (FeignException e) {
            throw new CustomException("Product with ID " + productId + " not found or Product Management Service unavailable");
        }
    }

    public PriceListResponse updatePriceList(UUID id, PriceListUpdateRequest request) {
        PriceList priceList = priceListRepository.findById(id)
                .orElseThrow(() -> new CustomException("Price list with ID " + id + " not found"));

        if (request.getProductId() != null) {
            validateProductId(request.getProductId());
            if (!priceList.getProductId().equals(request.getProductId()) &&
                    priceListRepository.existsByProductIdAndCustomerCategory(request.getProductId(), request.getCustomerCategory())) {
                throw new CustomException("Price list for product ID " + request.getProductId() +
                        " and customer category " + request.getCustomerCategory() + " already exists");
            }
            priceList.setProductId(request.getProductId());
        }
        if (request.getCustomerCategory() != null) {
            priceList.setCustomerCategory(request.getCustomerCategory());
        }
        if (request.getPrice() != null) {
            priceList.setPrice(request.getPrice());
        }
        if (request.getEffectiveDate() != null) {
            priceList.setEffectiveDate(request.getEffectiveDate());
        }

        PriceList updatedPriceList = priceListRepository.save(priceList);
        return mapToPriceListResponse(updatedPriceList);
    }

    public void deletePriceList(UUID id) {
        if (!priceListRepository.existsById(id)) {
            throw new CustomException("Price list with ID " + id + " not found");
        }
        priceListRepository.deleteById(id);
    }

    private void validateProductId(UUID productId) {
        try {
            ProductDto product = productFeignClient.getProductById(productId);
//            System.out.println("$$$$$$$$$$$$$$$$$$$$"+product);
            if (product == null || product.getId() == null) {
                throw new CustomException("Product with ID " + productId + " not found");
            }
        } catch (FeignException e) {
            throw new CustomException("Product with ID " + productId + " not found or Product Management Service unavailable");
        }
    }

    private PriceListResponse mapToPriceListResponse(PriceList priceList) {
        PriceListResponse response = new PriceListResponse();
        response.setId(priceList.getId());
        response.setProductId(priceList.getProductId());
        response.setCustomerCategory(priceList.getCustomerCategory());
        response.setPrice(priceList.getPrice());
        response.setEffectiveDate(priceList.getEffectiveDate());
        response.setCreatedAt(priceList.getCreatedAt());
        response.setUpdatedAt(priceList.getUpdatedAt());
        return response;
    }
}

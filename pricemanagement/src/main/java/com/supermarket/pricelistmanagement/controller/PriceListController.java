package com.supermarket.pricelistmanagement.controller;

import com.supermarket.pricelistmanagement.dto.request.PriceListCreateRequest;
import com.supermarket.pricelistmanagement.dto.request.PriceListUpdateRequest;
import com.supermarket.pricelistmanagement.dto.response.PriceListResponse;
import com.supermarket.pricelistmanagement.service.PriceListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/price-lists")
@RequiredArgsConstructor
public class PriceListController {

    private final PriceListService priceListService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_PRICE_LIST')")
    public ResponseEntity<PriceListResponse> createPriceList(@Valid @RequestBody PriceListCreateRequest request) {
        return ResponseEntity.ok(priceListService.createPriceList(request));
    }

    @GetMapping("/{productId}/prices")
    @PreAuthorize("hasAuthority('READ_PRICE_LIST')")
    public ResponseEntity<PriceListResponse> getPriceByProductId(@PathVariable UUID productId) {
        PriceListResponse priceList = priceListService.getPriceByProductId(productId);
        return ResponseEntity.ok(priceList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_PRICE_LIST')")
    public ResponseEntity<PriceListResponse> getPriceListById(@PathVariable UUID id) {
        return ResponseEntity.ok(priceListService.getPriceListById(id));
    }

    @GetMapping("/{id}/price")
    @PreAuthorize("hasAuthority('READ_PRICE_LIST')")
    public ResponseEntity<BigDecimal> getProductPrice(@PathVariable UUID id) {
        PriceListResponse priceList = priceListService.getPriceListById(id);
        return ResponseEntity.ok(priceList.getPrice()); // Assuming PriceListResponse has a getPrice() method
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_PRICE_LIST')")
    public ResponseEntity<Page<PriceListResponse>> getAllPriceLists(Pageable pageable) {
        return ResponseEntity.ok(priceListService.getAllPriceLists(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_PRICE_LIST')")
    public ResponseEntity<PriceListResponse> updatePriceList(@PathVariable UUID id, @Valid @RequestBody PriceListUpdateRequest request) {
        return ResponseEntity.ok(priceListService.updatePriceList(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_PRICE_LIST')")
    public ResponseEntity<Void> deletePriceList(@PathVariable UUID id) {
        priceListService.deletePriceList(id);
        return ResponseEntity.noContent().build();
    }
}
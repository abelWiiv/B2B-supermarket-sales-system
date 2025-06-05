package com.supermarket.shopmanagement.controller;

import com.supermarket.shopmanagement.dto.request.ShopCreateRequest;
import com.supermarket.shopmanagement.dto.request.ShopUpdateRequest;
import com.supermarket.shopmanagement.dto.response.ShopResponse;
import com.supermarket.shopmanagement.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_SHOP')")
    public ResponseEntity<ShopResponse> createShop(@Valid @RequestBody ShopCreateRequest request) {
        return ResponseEntity.ok(shopService.createShop(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_SHOP')")
    public ResponseEntity<ShopResponse> getShopById(@PathVariable UUID id) {
        return ResponseEntity.ok(shopService.getShopById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_SHOP')")
    public ResponseEntity<Page<ShopResponse>> getAllShops(Pageable pageable) {
        return ResponseEntity.ok(shopService.getAllShops(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_SHOP')")
    public ResponseEntity<ShopResponse> updateShop(@PathVariable UUID id, @Valid @RequestBody ShopUpdateRequest request) {
        return ResponseEntity.ok(shopService.updateShop(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_SHOP')")
    public ResponseEntity<Void> deleteShop(@PathVariable UUID id) {
        shopService.deleteShop(id);
        return ResponseEntity.noContent().build();
    }
}
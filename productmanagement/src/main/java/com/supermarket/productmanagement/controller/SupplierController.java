package com.supermarket.productmanagement.controller;

import com.supermarket.productmanagement.dto.request.SupplierCreateRequest;
import com.supermarket.productmanagement.dto.request.SupplierUpdateRequest;
import com.supermarket.productmanagement.dto.response.SupplierResponse;
import com.supermarket.productmanagement.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_SUPPLIER')")
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierCreateRequest request) {
        return ResponseEntity.ok(supplierService.createSupplier(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_SUPPLIER')")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable UUID id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_SUPPLIER')")
    public ResponseEntity<Page<SupplierResponse>> getAllSuppliers(Pageable pageable) {
        return ResponseEntity.ok(supplierService.getAllSuppliers(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_SUPPLIER')")
    public ResponseEntity<SupplierResponse> updateSupplier(@PathVariable UUID id, @Valid @RequestBody SupplierUpdateRequest request) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_SUPPLIER')")
    public ResponseEntity<Void> deleteSupplier(@PathVariable UUID id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
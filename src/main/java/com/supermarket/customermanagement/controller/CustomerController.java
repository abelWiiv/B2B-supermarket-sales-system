package com.supermarket.customermanagement.controller;

import com.supermarket.customermanagement.dto.request.CustomerCreateRequest;
import com.supermarket.customermanagement.dto.request.CustomerUpdateRequest;
import com.supermarket.customermanagement.dto.response.CustomerResponse;
import com.supermarket.customermanagement.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_CUSTOMER')")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerCreateRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_CUSTOMER')")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_CUSTOMER')")
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(Pageable pageable) {
        return ResponseEntity.ok(customerService.getAllCustomers(pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CUSTOMER')")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable UUID id, @Valid @RequestBody CustomerUpdateRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_CUSTOMER')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
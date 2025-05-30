package com.supermarket.productmanagement.service;

import com.supermarket.productmanagement.dto.request.SupplierCreateRequest;
import com.supermarket.productmanagement.dto.request.SupplierUpdateRequest;
import com.supermarket.productmanagement.dto.response.SupplierResponse;
import com.supermarket.productmanagement.exception.CustomException;
import com.supermarket.productmanagement.model.Supplier;
import com.supermarket.productmanagement.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierResponse createSupplier(SupplierCreateRequest request) {
        if (supplierRepository.existsByName(request.getName())) {
            throw new CustomException("Supplier with name " + request.getName() + " already exists");
        }

        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .contactDetails(request.getContactDetails())
                .build();

        Supplier savedSupplier = supplierRepository.save(supplier);
        return mapToSupplierResponse(savedSupplier);
    }

    public SupplierResponse getSupplierById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new CustomException("Supplier with ID " + id + " not found"));
        return mapToSupplierResponse(supplier);
    }

    public Page<SupplierResponse> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(this::mapToSupplierResponse);
    }

    public SupplierResponse updateSupplier(UUID id, SupplierUpdateRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new CustomException("Supplier with ID " + id + " not found"));

        if (!supplier.getName().equals(request.getName()) && supplierRepository.existsByName(request.getName())) {
            throw new CustomException("Supplier with name " + request.getName() + " already exists");
        }

        supplier.setName(request.getName());
        supplier.setContactDetails(request.getContactDetails());
        Supplier updatedSupplier = supplierRepository.save(supplier);
        return mapToSupplierResponse(updatedSupplier);
    }

    public void deleteSupplier(UUID id) {
        if (!supplierRepository.existsById(id)) {
            throw new CustomException("Supplier with ID " + id + " not found");
        }
        supplierRepository.deleteById(id);
    }

    private SupplierResponse mapToSupplierResponse(Supplier supplier) {
        SupplierResponse response = new SupplierResponse();
        response.setId(supplier.getId());
        response.setName(supplier.getName());
        response.setContactDetails(supplier.getContactDetails());
        response.setCreatedAt(supplier.getCreatedAt());
        response.setUpdatedAt(supplier.getUpdatedAt());
        return response;
    }
}
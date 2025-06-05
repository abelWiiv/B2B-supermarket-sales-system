package com.supermarket.productmanagement.service;

import com.supermarket.productmanagement.dto.request.ProductCreateRequest;
import com.supermarket.productmanagement.dto.request.ProductUpdateRequest;
import com.supermarket.productmanagement.dto.response.ProductResponse;
import com.supermarket.productmanagement.exception.CustomException;
import com.supermarket.productmanagement.model.Product;
import com.supermarket.productmanagement.repository.ProductRepository;
import com.supermarket.productmanagement.repository.CategoryRepository;
import com.supermarket.productmanagement.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public ProductResponse createProduct(ProductCreateRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new CustomException("Product with name " + request.getName() + " already exists");
        }
        if (!categoryRepository.existsById(request.getCategoryId())) {
            throw new CustomException("Category with ID " + request.getCategoryId() + " not found");
        }
        if (!supplierRepository.existsById(request.getSupplierId())) {
            throw new CustomException("Supplier with ID " + request.getSupplierId() + " not found");
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .supplierId(request.getSupplierId())
                .unitOfMeasure(request.getUnitOfMeasure())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException("Product with ID " + id + " not found"));
        return mapToProductResponse(product);
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToProductResponse);
    }

    public ProductResponse updateProduct(UUID id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException("Product with ID " + id + " not found"));

        if (!product.getName().equals(request.getName()) && productRepository.existsByName(request.getName())) {
            throw new CustomException("Product with name " + request.getName() + " already exists");
        }
        if (request.getCategoryId() != null && !categoryRepository.existsById(request.getCategoryId())) {
            throw new CustomException("Category with ID " + request.getCategoryId() + " not found");
        }
        if (request.getSupplierId() != null && !supplierRepository.existsById(request.getSupplierId())) {
            throw new CustomException("Supplier with ID " + request.getSupplierId() + " not found");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        if (request.getCategoryId() != null) {
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getSupplierId() != null) {
            product.setSupplierId(request.getSupplierId());
        }
        product.setUnitOfMeasure(request.getUnitOfMeasure());

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new CustomException("Product with ID " + id + " not found");
        }
        productRepository.deleteById(id);
    }

    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setCategoryId(product.getCategoryId());
        response.setSupplierId(product.getSupplierId());
        response.setUnitOfMeasure(product.getUnitOfMeasure());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}
package com.supermarket.shopmanagement.service;

import com.supermarket.shopmanagement.dto.request.ShopCreateRequest;
import com.supermarket.shopmanagement.dto.request.ShopUpdateRequest;
import com.supermarket.shopmanagement.dto.response.ShopResponse;
import com.supermarket.shopmanagement.exception.CustomException;
import com.supermarket.shopmanagement.model.Shop;
import com.supermarket.shopmanagement.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    public ShopResponse createShop(ShopCreateRequest request) {
        if (shopRepository.existsByName(request.getName())) {
            throw new CustomException("Shop with name " + request.getName() + " already exists");
        }

        Shop shop = Shop.builder()
                .name(request.getName())
                .location(request.getLocation())
                .managerContact(request.getManagerContact())
                .build();

        Shop savedShop = shopRepository.save(shop);
        return mapToShopResponse(savedShop);
    }

    public ShopResponse getShopById(UUID id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new CustomException("Shop with ID " + id + " not found"));
        return mapToShopResponse(shop);
    }

    public Page<ShopResponse> getAllShops(Pageable pageable) {
        return shopRepository.findAll(pageable)
                .map(this::mapToShopResponse);
    }

    public ShopResponse updateShop(UUID id, ShopUpdateRequest request) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new CustomException("Shop with ID " + id + " not found"));

        if (!shop.getName().equals(request.getName()) && shopRepository.existsByName(request.getName())) {
            throw new CustomException("Shop with name " + request.getName() + " already exists");
        }

        shop.setName(request.getName());
        if (request.getLocation() != null) {
            shop.setLocation(request.getLocation());
        }
        if (request.getManagerContact() != null) {
            shop.setManagerContact(request.getManagerContact());
        }

        Shop updatedShop = shopRepository.save(shop);
        return mapToShopResponse(updatedShop);
    }

    public void deleteShop(UUID id) {
        if (!shopRepository.existsById(id)) {
            throw new CustomException("Shop with ID " + id + " not found");
        }
        shopRepository.deleteById(id);
    }

    private ShopResponse mapToShopResponse(Shop shop) {
        ShopResponse response = new ShopResponse();
        response.setId(shop.getId());
        response.setName(shop.getName());
        response.setLocation(shop.getLocation());
        response.setManagerContact(shop.getManagerContact());
        response.setCreatedAt(shop.getCreatedAt());
        response.setUpdatedAt(shop.getUpdatedAt());
        return response;
    }
}
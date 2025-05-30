package com.supermarket.shopmanagement.service;

import com.supermarket.shopmanagement.dto.request.ShopCreateRequest;
import com.supermarket.shopmanagement.dto.request.ShopUpdateRequest;
import com.supermarket.shopmanagement.dto.response.ShopResponse;
import com.supermarket.shopmanagement.exception.CustomException;
import com.supermarket.shopmanagement.model.Shop;
import com.supermarket.shopmanagement.repository.ShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private ShopService shopService;

    private Shop shop;
    private UUID shopId;
    private ShopCreateRequest createRequest;
    private ShopUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        shopId = UUID.randomUUID();
        shop = Shop.builder()
                .id(shopId)
                .name("Test Shop")
                .location("Test Location")
                .managerContact("1234567890")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = new ShopCreateRequest();
        createRequest.setName("Test Shop");
        createRequest.setLocation("Test Location");
        createRequest.setManagerContact("1234567890");

        updateRequest = new ShopUpdateRequest();
        updateRequest.setName("Updated Shop");
        updateRequest.setLocation("Updated Location");
        updateRequest.setManagerContact("0987654321");
    }

    // Original Test Cases
    @Test
    void createShop_Success() {
        when(shopRepository.existsByName(createRequest.getName())).thenReturn(false);
        when(shopRepository.save(any(Shop.class))).thenReturn(shop);

        ShopResponse response = shopService.createShop(createRequest);

        assertNotNull(response);
        assertEquals(shop.getId(), response.getId());
        assertEquals(shop.getName(), response.getName());
        assertEquals(shop.getLocation(), response.getLocation());
        assertEquals(shop.getManagerContact(), response.getManagerContact());
        verify(shopRepository).save(any(Shop.class));
    }

    @Test
    void createShop_ShopNameExists_ThrowsException() {
        when(shopRepository.existsByName(createRequest.getName())).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class,
                () -> shopService.createShop(createRequest));

        assertEquals("Shop with name " + createRequest.getName() + " already exists",
                exception.getMessage());
        verify(shopRepository, never()).save(any(Shop.class));
    }

    @Test
    void getShopById_Success() {
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        ShopResponse response = shopService.getShopById(shopId);

        assertNotNull(response);
        assertEquals(shop.getId(), response.getId());
        assertEquals(shop.getName(), response.getName());
        verify(shopRepository).findById(shopId);
    }

    @Test
    void getShopById_NotFound_ThrowsException() {
        when(shopRepository.findById(shopId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> shopService.getShopById(shopId));

        assertEquals("Shop with ID " + shopId + " not found", exception.getMessage());
        verify(shopRepository).findById(shopId);
    }

    @Test
    void getAllShops_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shop> shopPage = new PageImpl<>(List.of(shop));
        when(shopRepository.findAll(pageable)).thenReturn(shopPage);

        Page<ShopResponse> response = shopService.getAllShops(pageable);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(shop.getName(), response.getContent().get(0).getName());
        verify(shopRepository).findAll(pageable);
    }

    @Test
    void updateShop_Success() {
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(shopRepository.existsByName(updateRequest.getName())).thenReturn(false);
        when(shopRepository.save(any(Shop.class))).thenReturn(shop);

        ShopResponse response = shopService.updateShop(shopId, updateRequest);

        assertNotNull(response);
        assertEquals(shop.getId(), response.getId());
        verify(shopRepository).save(any(Shop.class));
    }

    @Test
    void updateShop_ShopNotFound_ThrowsException() {
        when(shopRepository.findById(shopId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> shopService.updateShop(shopId, updateRequest));

        assertEquals("Shop with ID " + shopId + " not found", exception.getMessage());
        verify(shopRepository, never()).save(any(Shop.class));
    }

    @Test
    void updateShop_DuplicateName_ThrowsException() {
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(shopRepository.existsByName(updateRequest.getName())).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class,
                () -> shopService.updateShop(shopId, updateRequest));

        assertEquals("Shop with name " + updateRequest.getName() + " already exists",
                exception.getMessage());
        verify(shopRepository, never()).save(any(Shop.class));
    }

    @Test
    void updateShop_PartialUpdate_Success() {
        updateRequest.setLocation(null);
        updateRequest.setManagerContact(null);
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(shopRepository.existsByName(updateRequest.getName())).thenReturn(false);
        when(shopRepository.save(any(Shop.class))).thenReturn(shop);

        ShopResponse response = shopService.updateShop(shopId, updateRequest);

        assertNotNull(response);
        assertEquals(shop.getId(), response.getId());
        verify(shopRepository).save(any(Shop.class));
    }

    @Test
    void deleteShop_Success() {
        when(shopRepository.existsById(shopId)).thenReturn(true);

        shopService.deleteShop(shopId);

        verify(shopRepository).deleteById(shopId);
    }

    @Test
    void deleteShop_NotFound_ThrowsException() {
        when(shopRepository.existsById(shopId)).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class,
                () -> shopService.deleteShop(shopId));

        assertEquals("Shop with ID " + shopId + " not found", exception.getMessage());
        verify(shopRepository, never()).deleteById(shopId);
    }

    // New Test Cases
    @Test
    void createShop_NullName_ThrowsException() {
        createRequest.setName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> shopService.createShop(createRequest));

        assertEquals("Shop name cannot be null", exception.getMessage());
        verify(shopRepository, never()).existsByName(any());
        verify(shopRepository, never()).save(any(Shop.class));
    }

    @Test
    void createShop_EmptyName_ThrowsException() {
        createRequest.setName("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> shopService.createShop(createRequest));

        assertEquals("Shop name cannot be empty", exception.getMessage());
        verify(shopRepository, never()).existsByName(any());
        verify(shopRepository, never()).save(any(Shop.class));
    }

    @Test
    void getAllShops_EmptyPage_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shop> emptyPage = new PageImpl<>(List.of());
        when(shopRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<ShopResponse> response = shopService.getAllShops(pageable);

        assertNotNull(response);
        assertEquals(0, response.getContent().size());
        verify(shopRepository).findAll(pageable);
    }

    @Test
    void getAllShops_LargePageSize_Success() {
        Pageable pageable = PageRequest.of(0, 1000);
        Page<Shop> shopPage = new PageImpl<>(List.of(shop));
        when(shopRepository.findAll(pageable)).thenReturn(shopPage);

        Page<ShopResponse> response = shopService.getAllShops(pageable);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(shop.getName(), response.getContent().get(0).getName());
        verify(shopRepository).findAll(pageable);
    }

    @Test
    void updateShop_NullName_ThrowsException() {
        updateRequest.setName(null);
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> shopService.updateShop(shopId, updateRequest));

        assertEquals("Shop name cannot be null", exception.getMessage());
        verify(shopRepository, never()).save(any(Shop.class));
    }

    @Test
    void updateShop_EmptyName_ThrowsException() {
        updateRequest.setName("");
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> shopService.updateShop(shopId, updateRequest));

        assertEquals("Shop name cannot be empty", exception.getMessage());
        verify(shopRepository, never()).save(any(Shop.class));
    }

    @Test
    void updateShop_SameName_Success() {
        updateRequest.setName(shop.getName()); // Same name as existing shop
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(shopRepository.save(any(Shop.class))).thenReturn(shop);

        ShopResponse response = shopService.updateShop(shopId, updateRequest);

        assertNotNull(response);
        assertEquals(shop.getId(), response.getId());
        assertEquals(shop.getName(), response.getName());
        verify(shopRepository).save(any(Shop.class));
        verify(shopRepository, never()).existsByName(any()); // No need to check name existence
    }

    @Test
    void mapToShopResponse_NullFields_HandlesGracefully() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Shop nullFieldsShop = Shop.builder()
                .id(shopId)
                .name("Test Shop")
                .build(); // Location, managerContact, createdAt, updatedAt are null

        // Use reflection to invoke private method mapToShopResponse
        Method mapToShopResponse = ShopService.class.getDeclaredMethod("mapToShopResponse", Shop.class);
        mapToShopResponse.setAccessible(true);
        ShopResponse response = (ShopResponse) mapToShopResponse.invoke(shopService, nullFieldsShop);

        assertNotNull(response);
        assertEquals(nullFieldsShop.getId(), response.getId());
        assertEquals(nullFieldsShop.getName(), response.getName());
        assertNull(response.getLocation());
        assertNull(response.getManagerContact());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }
}
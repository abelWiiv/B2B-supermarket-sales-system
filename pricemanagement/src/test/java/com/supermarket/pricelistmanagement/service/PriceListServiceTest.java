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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceListServiceTest {

    @Mock
    private PriceListRepository priceListRepository;

    @Mock
    private ProductFeignClient productFeignClient;

    @InjectMocks
    private PriceListService priceListService;

    private UUID priceListId;
    private UUID productId;
    private PriceList priceList;
    private PriceListCreateRequest createRequest;
    private PriceListUpdateRequest updateRequest;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        priceListId = UUID.randomUUID();
        productId = UUID.randomUUID();

        priceList = PriceList.builder()
                .id(priceListId)
                .productId(productId)
                .customerCategory("REGULAR")
                .price(new BigDecimal("10.99"))
                .effectiveDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = new PriceListCreateRequest();
        createRequest.setProductId(productId);
        createRequest.setCustomerCategory("REGULAR");
        createRequest.setPrice(new BigDecimal("10.99"));
        createRequest.setEffectiveDate(LocalDate.now());

        updateRequest = new PriceListUpdateRequest();
        updateRequest.setPrice(new BigDecimal("12.99"));
        updateRequest.setCustomerCategory("PREMIUM");
        updateRequest.setEffectiveDate(LocalDate.now().plusDays(1));

        productDto = new ProductDto();
        productDto.setId(productId);
        productDto.setName("Test Product");
    }

    @Test
    void createPriceList_Success() {
        when(productFeignClient.getProductById(productId)).thenReturn(productDto);
        when(priceListRepository.existsByProductIdAndCustomerCategory(productId, "REGULAR")).thenReturn(false);
        when(priceListRepository.save(any(PriceList.class))).thenReturn(priceList);

        PriceListResponse response = priceListService.createPriceList(createRequest);

        assertNotNull(response);
        assertEquals(priceListId, response.getId());
        assertEquals(productId, response.getProductId());
        assertEquals("REGULAR", response.getCustomerCategory());
        assertEquals(new BigDecimal("10.99"), response.getPrice());
        verify(productFeignClient).getProductById(productId);
        verify(priceListRepository).save(any(PriceList.class));
    }

    @Test
    void createPriceList_ProductNotFound_ThrowsException() {
        when(productFeignClient.getProductById(productId)).thenThrow(new FeignException.NotFound("Product not found", null, null, null));

        CustomException exception = assertThrows(CustomException.class, () ->
                priceListService.createPriceList(createRequest));

        assertEquals("Product with ID " + productId + " not found or Product Management Service unavailable", exception.getMessage());
        verify(productFeignClient).getProductById(productId);
        verify(priceListRepository, never()).save(any());
    }

    @Test
    void createPriceList_DuplicatePriceList_ThrowsException() {
        when(productFeignClient.getProductById(productId)).thenReturn(productDto);
        when(priceListRepository.existsByProductIdAndCustomerCategory(productId, "REGULAR")).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class, () ->
                priceListService.createPriceList(createRequest));

        assertEquals("Price list for product ID " + productId + " and customer category REGULAR already exists",
                exception.getMessage());
        verify(priceListRepository, never()).save(any());
    }

    @Test
    void getPriceListById_Success() {
        when(priceListRepository.findById(priceListId)).thenReturn(Optional.of(priceList));

        PriceListResponse response = priceListService.getPriceListById(priceListId);

        assertNotNull(response);
        assertEquals(priceListId, response.getId());
        assertEquals(productId, response.getProductId());
        verify(priceListRepository).findById(priceListId);
    }

    @Test
    void getPriceListById_NotFound_ThrowsException() {
        when(priceListRepository.findById(priceListId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                priceListService.getPriceListById(priceListId));

        assertEquals("Price list with ID " + priceListId + " not found", exception.getMessage());
    }

    @Test
    void getAllPriceLists_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PriceList> page = new PageImpl<>(Arrays.asList(priceList));
        when(priceListRepository.findAll(pageable)).thenReturn(page);

        Page<PriceListResponse> response = priceListService.getAllPriceLists(pageable);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(priceListId, response.getContent().get(0).getId());
        verify(priceListRepository).findAll(pageable);
    }

    @Test
    void updatePriceList_Success() {
        when(priceListRepository.findById(priceListId)).thenReturn(Optional.of(priceList));
        when(productFeignClient.getProductById(productId)).thenReturn(productDto);
        when(priceListRepository.save(any(PriceList.class))).thenReturn(priceList);

        updateRequest.setProductId(productId);
        PriceListResponse response = priceListService.updatePriceList(priceListId, updateRequest);

        assertNotNull(response);
        assertEquals(priceListId, response.getId());
        assertEquals(new BigDecimal("12.99"), response.getPrice());
        assertEquals("PREMIUM", response.getCustomerCategory());
        verify(priceListRepository).save(any(PriceList.class));
    }

    @Test
    void updatePriceList_NotFound_ThrowsException() {
        when(priceListRepository.findById(priceListId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () ->
                priceListService.updatePriceList(priceListId, updateRequest));

        assertEquals("Price list with ID " + priceListId + " not found", exception.getMessage());
        verify(priceListRepository, never()).save(any());
    }

    @Test
    void deletePriceList_Success() {
        when(priceListRepository.existsById(priceListId)).thenReturn(true);

        priceListService.deletePriceList(priceListId);

        verify(priceListRepository).deleteById(priceListId);
    }

    @Test
    void deletePriceList_NotFound_ThrowsException() {
        when(priceListRepository.existsById(priceListId)).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class, () ->
                priceListService.deletePriceList(priceListId));

        assertEquals("Price list with ID " + priceListId + " not found", exception.getMessage());
        verify(priceListRepository, never()).deleteById(any());
    }

    @Test
    void getProductById_Success() {
        when(productFeignClient.getProductById(productId)).thenReturn(productDto);

        ProductDto result = priceListService.getProductById(productId);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(productFeignClient).getProductById(productId);
    }

    @Test
    void getProductById_FeignException_ThrowsCustomException() {
        when(productFeignClient.getProductById(productId))
                .thenThrow(new FeignException.NotFound("Product not found", null, null, null));

        CustomException exception = assertThrows(CustomException.class, () ->
                priceListService.getProductById(productId));

        assertEquals("Product with ID " + productId + " not found or Product Management Service unavailable",
                exception.getMessage());
    }
}
package com.supermarket.salesmanagement.service;

import com.supermarket.common.dto.PriceListResponse;
import com.supermarket.salesmanagement.dto.request.SalesOrderCreateRequest;
import com.supermarket.salesmanagement.dto.request.SalesOrderItemAddRequest;
import com.supermarket.salesmanagement.dto.request.SalesOrderUpdateRequest;
import com.supermarket.salesmanagement.dto.response.SalesOrderResponse;
import com.supermarket.salesmanagement.event.OrderStatusEvent;
import com.supermarket.salesmanagement.event.OrderStatusPublisher;
import com.supermarket.salesmanagement.exception.CustomException;
import com.supermarket.salesmanagement.model.Invoice;
import com.supermarket.salesmanagement.model.SalesOrder;
import com.supermarket.salesmanagement.model.SalesOrderItem;
import com.supermarket.salesmanagement.model.enums.OrderStatus;
import com.supermarket.salesmanagement.model.enums.PaymentStatus;
import com.supermarket.salesmanagement.repository.InvoiceRepository;
import com.supermarket.salesmanagement.repository.SalesOrderItemRepository;
import com.supermarket.salesmanagement.repository.SalesOrderRepository;
import com.supermarket.salesmanagement.service.client.CustomerClient;
import com.supermarket.salesmanagement.service.client.PriceListClient;
import com.supermarket.salesmanagement.service.client.ProductClient;
import com.supermarket.salesmanagement.service.client.ShopClient;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private SalesOrderItemRepository salesOrderItemRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private ShopClient shopClient;

    @Mock
    private PriceListClient priceListClient;

    @Mock
    private OrderStatusPublisher orderStatusPublisher;

    @InjectMocks
    private SalesOrderService salesOrderService;

    private UUID orderId;
    private UUID customerId;
    private UUID shopId;
    private UUID productId;
    private SalesOrder salesOrder;
    private SalesOrderCreateRequest createRequest;
    private SalesOrderUpdateRequest updateRequest;
    private SalesOrderItemAddRequest itemAddRequest;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        shopId = UUID.randomUUID();
        productId = UUID.randomUUID();

        salesOrder = SalesOrder.builder()
                .id(orderId)
                .customerId(customerId)
                .shopId(shopId)
                .orderDate(LocalDate.now())
                .status(OrderStatus.DRAFT)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

//        createRequest = new SalesOrderCreateRequest();
//        createRequest.setCustomerId(customerId);
//        createRequest.setShopId(shopId);
//        createRequest.setOrderDate(LocalDate.now());
//        createRequest.setItems(Collections.singletonList(
//                new SalesOrderCreateRequest.OrderItemRequest(productId)
//        ));

//        updateRequest = new SalesOrderUpdateRequest();
//        updateRequest.setItems(Collections.singletonList(
//                new SalesOrderUpdateRequest.OrderItemRequest(productId, 3)
//        ));

        itemAddRequest = new SalesOrderItemAddRequest();
        itemAddRequest.setProductId(productId);
        itemAddRequest.setQuantity(1);
    }

//    @Test
//    void createSalesOrder_Success() {
//        // Arrange
//        PriceListResponse priceList = new PriceListResponse();
//        priceList.setPrice(new BigDecimal("10.00"));
//        when(customerClient.getCustomerById(customerId)).thenReturn(null);
//        when(shopClient.getShopById(shopId)).thenReturn(null);
//        when(productClient.getProductById(productId)).thenReturn(null);
//        when(priceListClient.getPriceByProductId(productId)).thenReturn(priceList);
//        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(salesOrder);
//
//        // Act
//        SalesOrderResponse response = salesOrderService.createSalesOrder(createRequest);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(orderId, response.getId());
//        assertEquals(customerId, response.getCustomerId());
//        assertEquals(shopId, response.getShopId());
//        assertEquals(2, response.getItems().get(0).getQuantity());
//        assertEquals(new BigDecimal("20.00"), response.getTotalAmount());
//        verify(salesOrderRepository, times(1)).save(any(SalesOrder.class));
//    }

//    @Test
//    void createSalesOrder_NullCustomerId_ThrowsException() {
//        // Arrange
//        createRequest.setCustomerId(null);
//
//        // Act & Assert
//        CustomException exception = assertThrows(CustomException.class, () -> salesOrderService.createSalesOrder(createRequest));
//        assertEquals("Customer ID is required", exception.getMessage());
//    }

//    @Test
//    void createSalesOrder_NullPrice_ThrowsException() {
//        // Arrange
//        when(customerClient.getCustomerById(customerId)).thenReturn(null);
//        when(shopClient.getShopById(shopId)).thenReturn(null);
//        when(productClient.getProductById(productId)).thenReturn(null);
//        when(priceListClient.getPriceByProductId(productId)).thenReturn(null);
//
//        // Act & Assert
//        CustomException exception = assertThrows(CustomException.class, () -> salesOrderService.createSalesOrder(createRequest));
//        assertEquals("Price for product ID " + productId + " not found", exception.getMessage());
//    }

//    @Test
//    void updateSalesOrder_Success() {
//        // Arrange
//        PriceListResponse priceList = new PriceListResponse();
//        priceList.setPrice(new BigDecimal("15.00"));
//        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(salesOrder));
//        when(customerClient.getCustomerById(any())).thenReturn(null);
//        when(shopClient.getShopById(any())).thenReturn(null);
//        when(productClient.getProductById(productId)).thenReturn(null);
//        when(priceListClient.getPriceByProductId(productId)).thenReturn(priceList);
//        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(salesOrder);
//        doNothing().when(orderStatusPublisher).publishOrderStatusEvent(any(OrderStatusEvent.class));
//
//        // Act
//        SalesOrderResponse response = salesOrderService.updateSalesOrder(orderId, updateRequest);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(orderId, response.getId());
//        assertEquals(3, response.getItems().get(0).getQuantity());
//        assertEquals(new BigDecimal("45.00"), response.getTotalAmount());
//        verify(salesOrderItemRepository, times(1)).deleteBySalesOrderId(orderId);
//        verify(salesOrderRepository, times(1)).save(any(SalesOrder.class));
//        verify(orderStatusPublisher, times(1)).publishOrderStatusEvent(any(OrderStatusEvent.class));
//    }

//    @Test
//    void updateSalesOrder_OrderNotFound_ThrowsException() {
//        // Arrange
//        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.empty());
//
//
//        // Act & Assert
//        CustomException exception = assertThrows(CustomException.class, () -> salesOrderService.updateSalesOrder(orderId, updateRequest));
//        assertEquals("Sales order with ID " + orderId + " not found", exception.getMessage());
//    }

    @Test
    void addSalesOrderItem_Success() {
        // Arrange
        PriceListResponse priceList = new PriceListResponse();
        priceList.setPrice(new BigDecimal("10.00"));
        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(salesOrder));
        when(productClient.getProductById(productId)).thenReturn(null);
        when(priceListClient.getPriceByProductId(productId)).thenReturn(priceList);
        when(salesOrderItemRepository.save(any(SalesOrderItem.class))).thenAnswer(i -> i.getArguments()[0]);
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(salesOrder);
        doNothing().when(orderStatusPublisher).publishOrderStatusEvent(any(OrderStatusEvent.class));

        // Act
        SalesOrderResponse response = salesOrderService.addSalesOrderItem(orderId, itemAddRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(new BigDecimal("10.00"), response.getTotalAmount());
        verify(salesOrderItemRepository, times(1)).save(any(SalesOrderItem.class));
        verify(salesOrderRepository, times(1)).save(any(SalesOrder.class));
    }

    @Test
    void addSalesOrderItem_ConfirmedOrder_ThrowsException() {
        // Arrange
        salesOrder.setStatus(OrderStatus.CONFIRMED);
        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(salesOrder));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> salesOrderService.addSalesOrderItem(orderId, itemAddRequest));
        assertEquals("Cannot add items to a confirmed order", exception.getMessage());
    }

    @Test
    void deleteSalesOrderItem_Success() {
        // Arrange
        SalesOrderItem item = SalesOrderItem.builder()
                .id(UUID.randomUUID())
                .salesOrder(salesOrder)
                .productId(productId)
                .quantity(1)
                .unitPrice(new BigDecimal("10.00"))
                .totalPrice(new BigDecimal("10.00"))
                .build();
        salesOrder.getItems().add(item);
        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(salesOrder));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(salesOrder);
        doNothing().when(salesOrderItemRepository).delete(any(SalesOrderItem.class));
        doNothing().when(orderStatusPublisher).publishOrderStatusEvent(any(OrderStatusEvent.class));

        // Act
        SalesOrderResponse response = salesOrderService.deleteSalesOrderItem(orderId, item.getId());

        // Assert
        assertNotNull(response);
        assertTrue(response.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getTotalAmount());
        verify(salesOrderItemRepository, times(1)).delete(any(SalesOrderItem.class));
        verify(salesOrderRepository, times(1)).save(any(SalesOrder.class));
    }

    @Test
    void confirmOrderAfterPayment_Success() {
        // Arrange
        Invoice invoice = new Invoice();
        invoice.setPaymentStatus(PaymentStatus.PAID);
        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(salesOrder));
        when(invoiceRepository.findBySalesOrderId(orderId)).thenReturn(Optional.of(invoice));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(salesOrder);
        doNothing().when(orderStatusPublisher).publishOrderStatusEvent(any(OrderStatusEvent.class));

        // Act
        SalesOrderResponse response = salesOrderService.confirmOrderAfterPayment(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(OrderStatus.CONFIRMED, response.getStatus());
        verify(salesOrderRepository, times(1)).save(any(SalesOrder.class));
        verify(orderStatusPublisher, times(1)).publishOrderStatusEvent(any(OrderStatusEvent.class));
    }

    @Test
    void confirmOrderAfterPayment_UnpaidInvoice_ThrowsException() {
        // Arrange
        Invoice invoice = new Invoice();
        invoice.setPaymentStatus(PaymentStatus.UNPAID);
        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(salesOrder));
        when(invoiceRepository.findBySalesOrderId(orderId)).thenReturn(Optional.of(invoice));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> salesOrderService.confirmOrderAfterPayment(orderId));
        assertEquals("Invoice for sales order " + orderId + " is not fully paid. Current status: UNPAID", exception.getMessage());
    }

    @Test
    void getSalesOrderById_Success() {
        // Arrange
        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(salesOrder));

        // Act
        SalesOrderResponse response = salesOrderService.getSalesOrderById(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(orderId, response.getId());
        verify(salesOrderRepository, times(1)).findById(orderId);
    }

    @Test
    void getAllSalesOrders_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<SalesOrder> page = new PageImpl<>(Collections.singletonList(salesOrder));
        when(salesOrderRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<SalesOrderResponse> response = salesOrderService.getAllSalesOrders(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(orderId, response.getContent().get(0).getId());
        verify(salesOrderRepository, times(1)).findAll(pageable);
    }

    @Test
    void deleteSalesOrder_Success() {
        // Arrange
        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(salesOrder));
        when(invoiceRepository.existsBySalesOrderId(orderId)).thenReturn(false);
        doNothing().when(salesOrderRepository).delete(any(SalesOrder.class));

        // Act
        salesOrderService.deleteSalesOrder(orderId);

        // Assert
        verify(salesOrderRepository, times(1)).delete(salesOrder);
    }

    @Test
    void deleteSalesOrder_HasInvoice_ThrowsException() {
        // Arrange
        when(salesOrderRepository.findById(orderId)).thenReturn(Optional.of(salesOrder));
        when(invoiceRepository.existsBySalesOrderId(orderId)).thenReturn(true);

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> salesOrderService.deleteSalesOrder(orderId));
        assertEquals("Cannot delete sales order with associated invoices", exception.getMessage());
    }
}
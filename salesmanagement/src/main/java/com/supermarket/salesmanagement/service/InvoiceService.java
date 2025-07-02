package com.supermarket.salesmanagement.service;

import com.supermarket.salesmanagement.dto.request.InvoiceCreateRequest;
import com.supermarket.salesmanagement.dto.request.InvoiceUpdateRequest;
import com.supermarket.salesmanagement.dto.request.SalesOrderUpdateRequest;
import com.supermarket.salesmanagement.dto.response.InvoiceResponse;
import com.supermarket.salesmanagement.exception.CustomException;
import com.supermarket.salesmanagement.model.Invoice;
import com.supermarket.salesmanagement.model.SalesOrder;
import com.supermarket.salesmanagement.model.enums.PaymentStatus;
import com.supermarket.salesmanagement.model.enums.OrderStatus;
import com.supermarket.salesmanagement.repository.InvoiceRepository;
import com.supermarket.salesmanagement.repository.SalesOrderRepository;
import com.supermarket.salesmanagement.service.client.LoyaltyClient;
import com.supermarket.salesmanagement.dto.LoyaltyAccount;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderService salesOrderService;
    private final LoyaltyClient loyaltyClient;

    @Value("${loyalty.points.redemption-rate:1.0}")
    private Double pointsRedemptionRate;

    @Transactional
    public InvoiceResponse createInvoice(InvoiceCreateRequest request, Integer pointsToRedeem) {
        SalesOrder salesOrder = salesOrderRepository.findById(request.getSalesOrderId())
                .orElseThrow(() -> new CustomException("Sales order with ID " + request.getSalesOrderId() + " not found"));

        if (salesOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new CustomException("Cannot create invoice for cancelled sales order with ID " + request.getSalesOrderId());
        }

        if (invoiceRepository.existsBySalesOrderId(request.getSalesOrderId())) {
            throw new CustomException("Invoice for sales order ID " + request.getSalesOrderId() + " already exists");
        }

        // Handle point redemption
        if (pointsToRedeem != null && pointsToRedeem > 0) {
            try {
                LoyaltyAccount account = loyaltyClient.getLoyaltyById(salesOrder.getCustomerId());
                if (account == null || account.getPointsBalance() < pointsToRedeem) {
                    throw new CustomException("Insufficient points for redemption");
                }

                Double discount = pointsToRedeem * pointsRedemptionRate;
                salesOrder.setPointsRedeemed(pointsToRedeem);
                salesOrder.setRedemptionDiscount(discount);

                loyaltyClient.redeemPoints(salesOrder.getCustomerId(), pointsToRedeem);

                salesOrder.setTotalAmount(salesOrder.getTotalAmount().subtract(BigDecimal.valueOf(discount)));
                if (salesOrder.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
                    throw new CustomException("Redemption discount cannot exceed order total");
                }
                salesOrderRepository.save(salesOrder);
            } catch (FeignException e) {
                throw new CustomException("Failed to communicate with loyalty service: " + e.getMessage());
            }
        }

        Invoice invoice = Invoice.builder()
                .salesOrderId(request.getSalesOrderId())
                .invoiceDate(request.getInvoiceDate())
                .paymentStatus(PaymentStatus.UNPAID)
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Update sales order status to PENDING
        SalesOrderUpdateRequest updateRequest = new SalesOrderUpdateRequest();
        updateRequest.setStatus(OrderStatus.PENDING);
        salesOrderService.updateSalesOrder(request.getSalesOrderId(), updateRequest);

        return mapToInvoiceResponse(savedInvoice);
    }

    public InvoiceResponse getInvoiceById(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new CustomException("Invoice with ID " + id + " not found"));
        return mapToInvoiceResponse(invoice);
    }

    public Page<InvoiceResponse> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable)
                .map(this::mapToInvoiceResponse);
    }

    public InvoiceResponse updateInvoice(UUID id, InvoiceUpdateRequest request) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new CustomException("Invoice with ID " + id + " not found"));

        if (request.getSalesOrderId() != null) {
            SalesOrder salesOrder = salesOrderRepository.findById(request.getSalesOrderId())
                    .orElseThrow(() -> new CustomException("Sales order with ID " + request.getSalesOrderId() + " not found"));

            if (salesOrder.getStatus() == OrderStatus.CANCELLED) {
                throw new CustomException("Cannot update invoice to use cancelled sales order with ID " + request.getSalesOrderId());
            }
            invoice.setSalesOrderId(request.getSalesOrderId());
        }
        if (request.getInvoiceDate() != null) {
            invoice.setInvoiceDate(request.getInvoiceDate());
        }
        if (request.getPaymentStatus() != null) {
            invoice.setPaymentStatus(request.getPaymentStatus());
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return mapToInvoiceResponse(updatedInvoice);
    }

    public void deleteInvoice(UUID id) {
        if (!invoiceRepository.existsById(id)) {
            throw new CustomException("Invoice with ID " + id + " not found");
        }
        invoiceRepository.deleteById(id);
    }

    private InvoiceResponse mapToInvoiceResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setSalesOrderId(invoice.getSalesOrderId());
        response.setInvoiceDate(invoice.getInvoiceDate());
        response.setPaymentStatus(invoice.getPaymentStatus());
        response.setCreatedAt(invoice.getCreatedAt());
        response.setUpdatedAt(invoice.getUpdatedAt());

        // Fetch the SalesOrder to get the total amount
        SalesOrder salesOrder = salesOrderRepository.findById(invoice.getSalesOrderId())
                .orElseThrow(() -> new CustomException("Sales order with ID " + invoice.getSalesOrderId() + " not found"));
        response.setTotalAmount(salesOrder.getTotalAmount());


//        // Calculate and set the redemption amount
//        if (salesOrder.getRedemptionDiscount() != null) {
//            response.setRedemptionAmount(BigDecimal.valueOf(salesOrder.getRedemptionDiscount()));
//        } else {
//            response.setRedemptionAmount(BigDecimal.ZERO);
//        }

        // Only set redemptionAmount if there was a redemption
        if (salesOrder.getRedemptionDiscount() != null && salesOrder.getRedemptionDiscount() > 0) {
            response.setRedemptionAmount(BigDecimal.valueOf(salesOrder.getRedemptionDiscount()));
        } // Else, it remains null and is excluded from JSON


        return response;
    }
}
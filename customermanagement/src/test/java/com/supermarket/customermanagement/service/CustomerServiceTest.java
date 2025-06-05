package com.supermarket.customermanagement.service;

import com.supermarket.customermanagement.dto.request.CustomerCreateRequest;
import com.supermarket.customermanagement.dto.request.CustomerUpdateRequest;
import com.supermarket.customermanagement.dto.response.CustomerResponse;
import com.supermarket.customermanagement.exception.CustomException;
import com.supermarket.customermanagement.model.Customer;
import com.supermarket.customermanagement.repository.CustomerRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerCreateRequest createRequest;
    private CustomerUpdateRequest updateRequest;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        customer = Customer.builder()
                .id(customerId)
                .companyName("Test Company")
                .contactPerson("John Doe")
                .address("123 Test St")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .vatRegistrationNumber("VAT123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = CustomerCreateRequest.builder()
                .companyName("Test Company")
                .contactPerson("John Doe")
                .address("123 Test St")
                .email("test@example.com")
                .phoneNumber("1234567890")
                .vatRegistrationNumber("VAT123")
                .build();

        updateRequest = CustomerUpdateRequest.builder()
                .companyName("Updated Company")
                .contactPerson("Jane Doe")
                .address("456 Update St")
                .email("updated@example.com")
                .phoneNumber("0987654321")
                .vatRegistrationNumber("VAT456")
                .build();
    }

    @Test
    void createCustomer_Success() {
        when(customerRepository.existsByEmail(any())).thenReturn(false);
        when(customerRepository.existsByVatRegistrationNumber(any())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponse response = customerService.createCustomer(createRequest);

        assertNotNull(response);
        assertEquals(customer.getId(), response.getId());
        assertEquals(createRequest.getCompanyName(), response.getCompanyName());
        assertEquals(createRequest.getEmail(), response.getEmail());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_EmailAlreadyInUse_ThrowsException() {
        when(customerRepository.existsByEmail(createRequest.getEmail())).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class,
                () -> customerService.createCustomer(createRequest));

        assertEquals("Email already in use", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void createCustomer_VatAlreadyInUse_ThrowsException() {
        when(customerRepository.existsByEmail(any())).thenReturn(false);
        when(customerRepository.existsByVatRegistrationNumber(createRequest.getVatRegistrationNumber())).thenReturn(true);

        CustomException exception = assertThrows(CustomException.class,
                () -> customerService.createCustomer(createRequest));

        assertEquals("VAT registration number already in use", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getCustomerById_Success() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getCustomerById(customerId);

        assertNotNull(response);
        assertEquals(customer.getId(), response.getId());
        assertEquals(customer.getCompanyName(), response.getCompanyName());
        verify(customerRepository).findById(customerId);
    }

    @Test
    void getCustomerById_NotFound_ThrowsException() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> customerService.getCustomerById(customerId));

        assertEquals("Customer not found with ID: " + customerId, exception.getMessage());
    }

    @Test
    void getAllCustomers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer));
        when(customerRepository.findAll(pageable)).thenReturn(customerPage);

        Page<CustomerResponse> result = customerService.getAllCustomers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(customer.getId(), result.getContent().get(0).getId());
        verify(customerRepository).findAll(pageable);
    }

    @Test
    void updateCustomer_Success() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByEmail(any())).thenReturn(false);
        when(customerRepository.existsByVatRegistrationNumber(any())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponse response = customerService.updateCustomer(customerId, updateRequest);

        assertNotNull(response);
        assertEquals(updateRequest.getCompanyName(), response.getCompanyName());
        assertEquals(updateRequest.getEmail(), response.getEmail());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updateCustomer_NotFound_ThrowsException() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,
                () -> customerService.updateCustomer(customerId, updateRequest));

        assertEquals("Customer not found with ID: " + customerId, exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_Success() {
        when(customerRepository.existsById(customerId)).thenReturn(true);

        customerService.deleteCustomer(customerId);

        verify(customerRepository).deleteById(customerId);
    }

    @Test
    void deleteCustomer_NotFound_ThrowsException() {
        when(customerRepository.existsById(customerId)).thenReturn(false);

        CustomException exception = assertThrows(CustomException.class,
                () -> customerService.deleteCustomer(customerId));

        assertEquals("Customer not found with ID: " + customerId, exception.getMessage());
        verify(customerRepository, never()).deleteById(customerId);
    }
}
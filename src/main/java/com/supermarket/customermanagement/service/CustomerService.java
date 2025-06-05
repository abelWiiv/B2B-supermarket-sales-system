package com.supermarket.customermanagement.service;

import com.supermarket.customermanagement.dto.request.CustomerCreateRequest;
import com.supermarket.customermanagement.dto.request.CustomerUpdateRequest;
import com.supermarket.customermanagement.dto.response.CustomerResponse;
import com.supermarket.customermanagement.exception.CustomException;
import com.supermarket.customermanagement.model.Customer;
import com.supermarket.customermanagement.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already in use");
        }
        if (request.getVatRegistrationNumber() != null && customerRepository.existsByVatRegistrationNumber(request.getVatRegistrationNumber())) {
            throw new CustomException("VAT registration number already in use");
        }

        Customer customer = Customer.builder()
                .companyName(request.getCompanyName())
                .contactPerson(request.getContactPerson())
                .address(request.getAddress())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .vatRegistrationNumber(request.getVatRegistrationNumber())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        return mapToCustomerResponse(savedCustomer);
    }

    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomException("Customer not found with ID: " + id));
        return mapToCustomerResponse(customer);
    }

    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(this::mapToCustomerResponse);
    }

    public CustomerResponse updateCustomer(UUID id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomException("Customer not found with ID: " + id));

        if (request.getEmail() != null && !request.getEmail().equals(customer.getEmail()) &&
                customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already in use");
        }
        if (request.getVatRegistrationNumber() != null && !request.getVatRegistrationNumber().equals(customer.getVatRegistrationNumber()) &&
                customerRepository.existsByVatRegistrationNumber(request.getVatRegistrationNumber())) {
            throw new CustomException("VAT registration number already in use");
        }

        if (request.getCompanyName() != null) {
            customer.setCompanyName(request.getCompanyName());
        }
        if (request.getContactPerson() != null) {
            customer.setContactPerson(request.getContactPerson());
        }
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }
        if (request.getEmail() != null) {
            customer.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            customer.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getVatRegistrationNumber() != null) {
            customer.setVatRegistrationNumber(request.getVatRegistrationNumber());
        }

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToCustomerResponse(updatedCustomer);
    }

    public void deleteCustomer(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .companyName(customer.getCompanyName())
                .contactPerson(customer.getContactPerson())
                .address(customer.getAddress())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .vatRegistrationNumber(customer.getVatRegistrationNumber())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
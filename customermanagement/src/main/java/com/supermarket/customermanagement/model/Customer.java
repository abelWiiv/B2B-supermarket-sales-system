package com.supermarket.customermanagement.model;

import com.supermarket.customermanagement.config.EmailEncryptorConverter;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "contact_person", nullable = false)
    private String contactPerson;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "email", nullable = false, unique = true)
    @Convert(converter = EmailEncryptorConverter.class)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "vat_registration_number", unique = true)
    private String vatRegistrationNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
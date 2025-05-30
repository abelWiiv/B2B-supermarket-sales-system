package com.supermarket.usermanagement.service;

import com.supermarket.usermanagement.dto.response.PrivilegeResponse;
import com.supermarket.usermanagement.exception.CustomException;
import com.supermarket.usermanagement.model.Privilege;
import com.supermarket.usermanagement.repository.PrivilegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrivilegeService {

    private final PrivilegeRepository privilegeRepository;

    public Page<PrivilegeResponse> getAllPrivileges(Pageable pageable) {
        return privilegeRepository.findAll(pageable)
                .map(this::mapToPrivilegeResponse);
    }

    public PrivilegeResponse getPrivilegeById(UUID id) {
        Privilege privilege = privilegeRepository.findById(id)
                .orElseThrow(() -> new CustomException("Privilege not found"));
        return mapToPrivilegeResponse(privilege);
    }

    public PrivilegeResponse createPrivilege(String name, String description) {
        if (privilegeRepository.existsByName(name)) {
            throw new CustomException("Privilege already exists");
        }

        Privilege privilege = Privilege.builder()
                .name(name)
                .description(description)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Privilege savedPrivilege = privilegeRepository.save(privilege);
        return mapToPrivilegeResponse(savedPrivilege);
    }

    public PrivilegeResponse updatePrivilege(UUID id, String name, String description) {
        Privilege privilege = privilegeRepository.findById(id)
                .orElseThrow(() -> new CustomException("Privilege not found"));

        if (name != null && !name.equals(privilege.getName()) && privilegeRepository.existsByName(name)) {
            throw new CustomException("Privilege name already exists");
        }

        if (name != null) {
            privilege.setName(name);
        }

        if (description != null) {
            privilege.setDescription(description);
        }

        privilege.setUpdatedAt(LocalDateTime.now());
        Privilege updatedPrivilege = privilegeRepository.save(privilege);
        return mapToPrivilegeResponse(updatedPrivilege);
    }

    public void deletePrivilege(UUID id) {
        if (!privilegeRepository.existsById(id)) {
            throw new CustomException("Privilege not found");
        }
        privilegeRepository.deleteById(id);
    }

    private PrivilegeResponse mapToPrivilegeResponse(Privilege privilege) {
        return PrivilegeResponse.builder()
                .id(privilege.getId())
                .name(privilege.getName())
                .description(privilege.getDescription())
                .build();
    }
}
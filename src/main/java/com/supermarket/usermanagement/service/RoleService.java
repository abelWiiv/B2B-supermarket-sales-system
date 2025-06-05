package com.supermarket.usermanagement.service;

import com.supermarket.usermanagement.dto.response.RoleResponse;
import com.supermarket.usermanagement.exception.CustomException;
import com.supermarket.usermanagement.model.Privilege;
import com.supermarket.usermanagement.model.Role;
import com.supermarket.usermanagement.repository.PrivilegeRepository;
import com.supermarket.usermanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    public Page<RoleResponse> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable)
                .map(this::mapToRoleResponse);
    }

    public RoleResponse getRoleById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Role not found"));
        return mapToRoleResponse(role);
    }

    public RoleResponse createRole(String name, String description, Set<UUID> privilegeIds) {
        if (roleRepository.existsByName(name)) {
            throw new CustomException("Role already exists");
        }

        Set<Privilege> privileges = privilegeIds.stream()
                .map(id -> privilegeRepository.findById(id)
                        .orElseThrow(() -> new CustomException("Privilege not found")))
                .collect(Collectors.toSet());

        Role role = Role.builder()
                .name(name)
                .description(description)
                .privileges(privileges)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Role savedRole = roleRepository.save(role);
        return mapToRoleResponse(savedRole);
    }

    public RoleResponse updateRole(UUID id, String name, String description, Set<UUID> privilegeIds) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new CustomException("Role not found"));

        if (name != null && !name.equals(role.getName()) && roleRepository.existsByName(name)) {
            throw new CustomException("Role name already exists");
        }

        if (name != null) {
            role.setName(name);
        }

        if (description != null) {
            role.setDescription(description);
        }

        if (privilegeIds != null) {
            Set<Privilege> privileges = privilegeIds.stream()
                    .map(privId -> privilegeRepository.findById(privId)
                            .orElseThrow(() -> new CustomException("Privilege not found")))
                    .collect(Collectors.toSet());
            role.setPrivileges(privileges);
        }

        role.setUpdatedAt(LocalDateTime.now());
        Role updatedRole = roleRepository.save(role);
        return mapToRoleResponse(updatedRole);
    }

    public void deleteRole(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new CustomException("Role not found");
        }
        roleRepository.deleteById(id);
    }

    private RoleResponse mapToRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .privileges(role.getPrivileges().stream()
                        .map(Privilege::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}
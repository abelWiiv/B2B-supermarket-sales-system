package com.supermarket.usermanagement.controller;

import com.supermarket.usermanagement.dto.response.RoleResponse;
import com.supermarket.usermanagement.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<RoleResponse>> getAllRoles(Pageable pageable) {
        return ResponseEntity.ok(roleService.getAllRoles(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoleResponse> createRole(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestBody Set<UUID> privilegeIds
    ) {
        return ResponseEntity.ok(roleService.createRole(name, description, privilegeIds));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestBody(required = false) Set<UUID> privilegeIds
    ) {
        return ResponseEntity.ok(roleService.updateRole(id, name, description, privilegeIds));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
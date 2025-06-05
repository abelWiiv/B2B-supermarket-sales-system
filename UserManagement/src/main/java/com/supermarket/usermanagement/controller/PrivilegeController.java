package com.supermarket.usermanagement.controller;

import com.supermarket.usermanagement.dto.response.PrivilegeResponse;
import com.supermarket.usermanagement.service.PrivilegeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/privileges")
@RequiredArgsConstructor
public class PrivilegeController {

    private final PrivilegeService privilegeService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<PrivilegeResponse>> getAllPrivileges(Pageable pageable) {
        return ResponseEntity.ok(privilegeService.getAllPrivileges(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PrivilegeResponse> getPrivilegeById(@PathVariable UUID id) {
        return ResponseEntity.ok(privilegeService.getPrivilegeById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PrivilegeResponse> createPrivilege(
            @RequestParam String name,
            @RequestParam(required = false) String description
    ) {

        return ResponseEntity.ok(privilegeService.createPrivilege(name, description));
//        return ResponseEntity.ok(privilegeService.createPrivilege
//                .name(name)
//                .description(description)
//                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PrivilegeResponse> updatePrivilege(
            @PathVariable UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description
    ) {
        return ResponseEntity.ok(privilegeService.updatePrivilege(id, name, description));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deletePrivilege(@PathVariable UUID id) {
        privilegeService.deletePrivilege(id);
        return ResponseEntity.noContent().build();
    }
}
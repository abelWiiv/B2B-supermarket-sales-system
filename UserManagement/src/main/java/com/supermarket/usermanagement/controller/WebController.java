package com.supermarket.usermanagement.controller;

import com.supermarket.usermanagement.dto.request.UpdateUserRequest;
import com.supermarket.usermanagement.service.PrivilegeService;
import com.supermarket.usermanagement.service.RoleService;
import com.supermarket.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final UserService userService;
    private final RoleService roleService;
    private final PrivilegeService privilegeService;

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String users(Model model, @PageableDefault(size = 10) Pageable pageable) {
        model.addAttribute("users", userService.getAllUsers(pageable));
        return "users";
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == principal.id")
    public String editUser(@PathVariable UUID id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("roles", roleService.getAllRoles(Pageable.unpaged()).getContent());
        return "user-edit";
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String roles(Model model, @PageableDefault(size = 10) Pageable pageable) {
        model.addAttribute("roles", roleService.getAllRoles(pageable));
        model.addAttribute("privileges", privilegeService.getAllPrivileges(Pageable.unpaged()).getContent());
        return "roles";
    }

    @GetMapping("/privileges")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String privileges(Model model, @PageableDefault(size = 10) Pageable pageable) {
        model.addAttribute("privileges", privilegeService.getAllPrivileges(pageable));
        return "privileges";
    }

    @GetMapping("/error")
    public String error(Model model) {
        model.addAttribute("error", "Access Denied");
        return "error";
    }
}
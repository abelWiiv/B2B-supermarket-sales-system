package com.supermarket.usermanagement.service;

import com.supermarket.usermanagement.dto.request.LoginRequest;
import com.supermarket.usermanagement.dto.request.RegisterRequest;
import com.supermarket.usermanagement.dto.request.RefreshTokenRequest;
import com.supermarket.usermanagement.dto.response.AuthResponse;
import com.supermarket.usermanagement.exception.CustomException;
import com.supermarket.usermanagement.model.Role;
import com.supermarket.usermanagement.model.User;
import com.supermarket.usermanagement.repository.RoleRepository;
import com.supermarket.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private User user;
    private Role userRole;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("refresh-token")
                .build();

        userRole = Role.builder()
                .name("ROLE_USER")
                .build();

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
    }

    @Test
    void register_SuccessfulRegistration_ReturnsAuthResponse() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
        verify(refreshTokenService).createRefreshToken(any(User.class), eq("refresh-token"));
    }

    @Test
    void register_UsernameTaken_ThrowsCustomException() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // Act & Assert
        assertThrows(CustomException.class, () -> authService.register(registerRequest),
                "Username is already taken");
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_EmailInUse_ThrowsCustomException() {
        // Arrange
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(CustomException.class, () -> authService.register(registerRequest),
                "Email is already in use");
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_SuccessfulLogin_ReturnsAuthResponse() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(securityContext).setAuthentication(authentication);
        verify(refreshTokenService).createRefreshToken(any(User.class), eq("refresh-token"));
    }

    @Test
    void login_UserNotFound_ThrowsCustomException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomException.class, () -> authService.login(loginRequest),
                "User not found");
    }

    @Test
    void refreshToken_ValidToken_ReturnsNewAuthResponse() {
        // Arrange
        when(jwtService.extractUsername(refreshTokenRequest.getRefreshToken())).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshTokenRequest.getRefreshToken(), user)).thenReturn(true);
        when(refreshTokenService.isRefreshTokenValid(user, refreshTokenRequest.getRefreshToken())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh-token");

        // Act
        AuthResponse response = authService.refreshToken(refreshTokenRequest);

        // Assert
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        verify(refreshTokenService).updateRefreshToken(eq(user), eq("refresh-token"), eq("new-refresh-token"));
    }

    @Test
    void refreshToken_InvalidToken_ThrowsCustomException() {
        // Arrange
        when(jwtService.extractUsername(refreshTokenRequest.getRefreshToken())).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshTokenRequest.getRefreshToken(), user)).thenReturn(false);

        // Act & Assert
        assertThrows(CustomException.class, () -> authService.refreshToken(refreshTokenRequest),
                "Invalid refresh token");
        verify(refreshTokenService, never()).updateRefreshToken(any(), any(), any());
    }

    @Test
    void refreshToken_InvalidRefreshToken_ThrowsCustomException() {
        // Arrange
        when(jwtService.extractUsername(refreshTokenRequest.getRefreshToken())).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(refreshTokenRequest.getRefreshToken(), user)).thenReturn(true);
        when(refreshTokenService.isRefreshTokenValid(user, refreshTokenRequest.getRefreshToken())).thenReturn(false);

        // Act & Assert
        assertThrows(CustomException.class, () -> authService.refreshToken(refreshTokenRequest),
                "Invalid refresh token");
        verify(refreshTokenService, never()).updateRefreshToken(any(), any(), any());
    }
}
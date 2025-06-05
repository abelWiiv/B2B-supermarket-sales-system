package com.supermarket.usermanagement.service;

import com.supermarket.usermanagement.exception.CustomException;
import com.supermarket.usermanagement.model.RefreshToken;
import com.supermarket.usermanagement.model.User;
import com.supermarket.usermanagement.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void createRefreshToken(User user, String token) {
        refreshTokenRepository.findByUserId(user.getId())
                .ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    public boolean isRefreshTokenValid(User user, String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException("Refresh token not found"));

        return refreshToken.getUser().getId().equals(user.getId()) &&
                refreshToken.getExpiryDate().isAfter(LocalDateTime.now());
    }

    public void updateRefreshToken(User user, String oldToken, String newToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(oldToken)
                .orElseThrow(() -> new CustomException("Refresh token not found"));

        refreshToken.setToken(newToken);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshToken);
    }
}
package com.vickey.skillforge.service;

import com.vickey.skillforge.entity.RefreshToken;
import com.vickey.skillforge.entity.User;
import com.vickey.skillforge.exception.InvalidRefreshTokenException;
import com.vickey.skillforge.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;
@Service
public class RefreshTokenService {
    private static final long REFRESH_TOKEN_DURATION_SECONDS =
            7 * 24 * 60 * 60;
    private final RefreshTokenRepository refreshTokenRepository;
    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                Instant.now().plusSeconds(
                        REFRESH_TOKEN_DURATION_SECONDS
                )
        );
        return refreshTokenRepository.save(refreshToken);
    }
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException(
                                        "Invalid refresh token"
                                )
                        );
        if (refreshToken.getExpiryDate()
                .isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidRefreshTokenException(
                    "Refresh token expired"
            );
        }
        return refreshToken;
    }
}
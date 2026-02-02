package pard.server.com.chapter6.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pard.server.com.chapter6.domain.RefreshToken;
import pard.server.com.chapter6.repository.RefreshTokenRepository;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}
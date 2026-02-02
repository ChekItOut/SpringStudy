package pard.server.com.chapter6.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pard.server.com.chapter6.domain.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
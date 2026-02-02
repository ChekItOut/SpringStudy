package pard.server.com.chapter6.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pard.server.com.chapter6.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

package br.com.geloteam.studentmanagement.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserDetails> findUserByEmail(String email);
    Optional<UserJpaEntity> findByResetToken(String resetToken);
}

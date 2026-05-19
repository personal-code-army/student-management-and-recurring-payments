package br.com.geloteam.studentmanagement.infrastructure.persistence.user;

import br.com.geloteam.studentmanagement.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<UserDetails> findUserByEmail(String email);
}

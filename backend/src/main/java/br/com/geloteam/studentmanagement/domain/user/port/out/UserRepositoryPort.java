package br.com.geloteam.studentmanagement.domain.user.port.out;

import br.com.geloteam.studentmanagement.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    User save(User user);
    void delete(User user);
}

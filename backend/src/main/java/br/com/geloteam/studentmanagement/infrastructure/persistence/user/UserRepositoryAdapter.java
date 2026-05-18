package br.com.geloteam.studentmanagement.infrastructure.persistence.user;

import br.com.geloteam.studentmanagement.domain.user.entity.User;
import br.com.geloteam.studentmanagement.domain.user.port.out.UserRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpa;

    public UserRepositoryAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Optional<User> findById(Long id) { return jpa.findById(id); }
    @Override public List<User> findAll() { return jpa.findAll(); }
    @Override public User save(User user) { return jpa.save(user); }
    @Override public void delete(User user) { jpa.delete(user); }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findUserByEmail(email).map(u -> (User) u);
    }
}

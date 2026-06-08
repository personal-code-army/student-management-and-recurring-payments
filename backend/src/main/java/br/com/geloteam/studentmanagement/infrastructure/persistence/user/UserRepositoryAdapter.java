package br.com.geloteam.studentmanagement.infrastructure.persistence.user;

import br.com.geloteam.studentmanagement.domain.user.entity.User;
import br.com.geloteam.studentmanagement.domain.user.port.out.UserRepositoryPort;
import br.com.geloteam.studentmanagement.infrastructure.persistence.company.CompanyJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpa;

    public UserRepositoryAdapter(UserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Optional<User> findById(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public List<User> findAll() { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public User save(User user) { return toDomain(jpa.save(toJpaEntity(user))); }
    @Override public void delete(User user) { jpa.deleteById(user.getId()); }
    @Override public Optional<User> findByEmail(String email) { return jpa.findUserByEmail(email).map(ud -> toDomain((UserJpaEntity) ud)); }
    @Override public Optional<User> findByResetToken(String resetToken) { return jpa.findByResetToken(resetToken).map(this::toDomain); }

    private User toDomain(UserJpaEntity e) {
        User u = new User();
        u.setId(e.getId());
        u.setCompanyId(e.getCompany() != null ? e.getCompany().getId() : null);
        u.setName(e.getName());
        u.setEmail(e.getEmail());
        u.setPassword(e.getPassword());
        u.setCellphoneNumber(e.getCellphoneNumber());
        u.setCpf(e.getCpf());
        u.setResetToken(e.getResetToken());
        u.setResetTokenExpiry(e.getResetTokenExpiry());
        u.setRole(e.getRole());
        return u;
    }

    private UserJpaEntity toJpaEntity(User u) {
        UserJpaEntity e = new UserJpaEntity();
        e.setId(u.getId());
        if (u.getCompanyId() != null) {
            CompanyJpaEntity company = new CompanyJpaEntity();
            company.setId(u.getCompanyId());
            e.setCompany(company);
        }
        e.setName(u.getName());
        e.setEmail(u.getEmail());
        e.setPassword(u.getPassword());
        e.setCellphoneNumber(u.getCellphoneNumber());
        e.setCpf(u.getCpf());
        e.setResetToken(u.getResetToken());
        e.setResetTokenExpiry(u.getResetTokenExpiry());
        e.setRole(u.getRole());
        return e;
    }
}

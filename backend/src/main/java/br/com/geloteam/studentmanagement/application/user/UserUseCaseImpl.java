package br.com.geloteam.studentmanagement.application.user;

import br.com.geloteam.studentmanagement.domain.user.entity.User;
import br.com.geloteam.studentmanagement.domain.user.port.in.ChangePasswordUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.DeleteUserUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.FindUserByIdUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.FindUserUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.UpdateUserUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.out.CompanyRepositoryPort;
import br.com.geloteam.studentmanagement.domain.user.port.out.UserRepositoryPort;
import br.com.geloteam.studentmanagement.shared.exception.ConflictException;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import br.com.geloteam.studentmanagement.shared.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserUseCaseImpl implements FindUserUseCase, FindUserByIdUseCase, UpdateUserUseCase, DeleteUserUseCase, ChangePasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final CompanyRepositoryPort companyRepository;
    private final PasswordEncoder passwordEncoder;

    public UserUseCaseImpl(UserRepositoryPort userRepository,
                           CompanyRepositoryPort companyRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com ID: " + id));
    }

    @Override
    @Transactional
    public User execute(Long id, String name, String email, String cellphoneNumber, Long companyId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com ID: " + id));

        if (email != null && !email.equals(user.getEmail())) {
            userRepository.findByEmail(email).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new ConflictException("EMAIL_ALREADY_EXISTS", "E-mail já cadastrado!");
                }
            });
            user.setEmail(email);
        }

        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company não encontrada com ID: " + companyId));

        user.setName(name);
        user.setCellphoneNumber(cellphoneNumber);
        user.setCompanyId(company.getId());

        User saved = userRepository.save(user);
        log.info("User {} updated", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public User execute(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com ID: " + id));
        userRepository.delete(user);
        log.info("User {} deleted", id);
        return user;
    }

    @Override
    @Transactional
    public void execute(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedException("Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", user.getId());
    }
}

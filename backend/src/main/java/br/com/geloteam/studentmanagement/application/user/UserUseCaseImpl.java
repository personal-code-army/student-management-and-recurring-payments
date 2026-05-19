package br.com.geloteam.studentmanagement.application.user;

import br.com.geloteam.studentmanagement.domain.user.entity.User;
import br.com.geloteam.studentmanagement.domain.user.port.in.DeleteUserUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.FindUserUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.UpdateUserUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.out.CompanyRepositoryPort;
import br.com.geloteam.studentmanagement.domain.user.port.out.UserRepositoryPort;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserUseCaseImpl implements FindUserUseCase, UpdateUserUseCase, DeleteUserUseCase {

    private final UserRepositoryPort userRepository;
    private final CompanyRepositoryPort companyRepository;

    public UserUseCaseImpl(UserRepositoryPort userRepository, CompanyRepositoryPort companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User execute(Long id, String name, String cellphoneNumber, Long companyId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com ID: " + id));

        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company não encontrada com ID: " + companyId));

        user.setName(name);
        user.setCellphoneNumber(cellphoneNumber);
        user.setCompany(company);

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
}

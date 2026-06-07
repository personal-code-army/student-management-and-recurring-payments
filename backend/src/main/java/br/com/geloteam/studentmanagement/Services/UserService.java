package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.DTO.UpdateUserDTO;
import br.com.geloteam.studentmanagement.DTO.auth.RegisterResponseDTO;
import br.com.geloteam.studentmanagement.Models.Company;
import br.com.geloteam.studentmanagement.Models.User;
import br.com.geloteam.studentmanagement.Repositories.UserRepository;
import br.com.geloteam.studentmanagement.exception.EntityNotFound;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyService companyService;

    public UserService(UserRepository userRepository, CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
    }

    public List<RegisterResponseDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(RegisterResponseDTO::new)
                .toList();
    }

    public RegisterResponseDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFound("Usuário não encontrado"));
        return new RegisterResponseDTO(user);
    }

    @Transactional
    public RegisterResponseDTO update(Long id, UpdateUserDTO data) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFound("Usuário não encontrado"));

        if (data.email() != null && !data.email().isBlank() && !data.email().equals(user.getEmail())) {
            userRepository.findByEmail(data.email()).ifPresent(existing -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já está em uso");
            });
            user.setEmail(data.email());
        }

        user.setName(data.name());
        user.setCellphoneNumber(data.cellphoneNumber());

        Company company = companyService.findById(data.companyId());
        user.setCompany(company);

        return new RegisterResponseDTO(userRepository.save(user));
    }

    @Transactional
    public RegisterResponseDTO delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFound("Usuário não encontrado"));
        userRepository.delete(user);
        return new RegisterResponseDTO(user);
    }
}

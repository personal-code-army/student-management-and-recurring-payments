package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.DTO.UpdateUserDTO;
import br.com.geloteam.studentmanagement.DTO.auth.RegisterResponseDTO;
import br.com.geloteam.studentmanagement.Models.Company;
import br.com.geloteam.studentmanagement.Models.User;
import br.com.geloteam.studentmanagement.Repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(RegisterResponseDTO::new)
                .toList();
    }

    @Transactional
    public RegisterResponseDTO update(Long id, UpdateUserDTO data) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));

        user.setName(data.name());
        user.setCellphoneNumber(data.cellphoneNumber());

        Company company = companyService.findById(data.companyId());
        user.setCompany(company);

        User savedUser = userRepository.save(user);
        return new RegisterResponseDTO(savedUser);
    }

    @Transactional
    public RegisterResponseDTO delete(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
        return new RegisterResponseDTO(user);
    }
}

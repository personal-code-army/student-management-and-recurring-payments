package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.DTO.UserResponseDTO;
import br.com.geloteam.studentmanagement.Models.User;
import br.com.geloteam.studentmanagement.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public List<UserResponseDTO> findAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(UserResponseDTO::new)
                .toList();
    }
}

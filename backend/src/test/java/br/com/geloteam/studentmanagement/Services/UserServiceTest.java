package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.DTO.UpdateUserDTO;
import br.com.geloteam.studentmanagement.DTO.auth.RegisterResponseDTO;
import br.com.geloteam.studentmanagement.Models.Company;
import br.com.geloteam.studentmanagement.Models.User;
import br.com.geloteam.studentmanagement.Repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should return all users mapped to DTOs")
    void findAllUsersShouldReturnDtoList() {
        // Arrange
        // 1. Criamos uma empresa fictícia para satisfazer a dependência do DTO
        Company company = new Company();
        company.setId(1L);

        // 2. Criamos os usuários e associamos a empresa a cada um deles
        User user1 = new User();
        user1.setName("Vitor");
        user1.setCompany(company); // Essencial para evitar o NPE

        User user2 = new User();
        user2.setName("John");
        user2.setCompany(company); // Essencial para evitar o NPE

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<RegisterResponseDTO> result = userService.findAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        // Verificamos se o ID mapeado no DTO é o mesmo da empresa que criamos
        assertEquals(1L, result.get(0).companyId());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update user successfully when ID exists")
    void updateShouldReturnUpdatedUserDtoWhenIdExists() {
        // Arrange
        Long userId = 1L;
        Long companyId = 10L;
        UpdateUserDTO dto = new UpdateUserDTO("Vitor Gonzalez", "11999999999", companyId);

        User existingUser = new User();
        existingUser.setId(userId);

        Company company = new Company();
        company.setId(companyId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(companyService.findById(companyId)).thenReturn(company);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RegisterResponseDTO result = userService.update(userId, dto);

        // Assert
        assertNotNull(result);
        assertEquals("Vitor Gonzalez", existingUser.getName());
        assertEquals(company, existingUser.getCompany());
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when updating non-existent user")
    void updateShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        Long userId = 1L;
        UpdateUserDTO dto = new UpdateUserDTO("Name", "123", 10L);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.update(userId, dto));
        verify(userRepository, never()).save(any());
        verify(companyService, never()).findById(any());
    }

    @Test
    @DisplayName("Should delete user and return DTO when ID exists")
    void deleteShouldRemoveUserAndReturnDto() {
        // Arrange
        Company company = new Company();
        company.setId(10L); // Dado obrigatório para o DTO

        User user = new User();
        user.setId(1L);
        user.setName("Vitor");
        user.setCompany(company); // Garantindo a integridade do objeto

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        RegisterResponseDTO result = userService.delete(1L);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.companyId());
        verify(userRepository, times(1)).delete(user);
    }
}
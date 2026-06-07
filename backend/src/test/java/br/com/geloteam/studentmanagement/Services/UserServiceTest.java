package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.DTO.UpdateUserDTO;
import br.com.geloteam.studentmanagement.DTO.auth.RegisterResponseDTO;
import br.com.geloteam.studentmanagement.Models.Company;
import br.com.geloteam.studentmanagement.Models.User;
import br.com.geloteam.studentmanagement.Models.UserRole;
import br.com.geloteam.studentmanagement.Repositories.UserRepository;
import br.com.geloteam.studentmanagement.exception.EntityNotFound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private User buildUser(Long id, String name, Long companyId) {
        Company company = new Company();
        company.setId(companyId);

        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setRole(UserRole.USER);
        user.setCompany(company);
        return user;
    }

    @Test
    @DisplayName("Should return all users mapped to DTOs")
    void findAllUsersShouldReturnDtoList() {
        User user1 = buildUser(1L, "Vitor", 1L);
        User user2 = buildUser(2L, "John", 1L);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<RegisterResponseDTO> result = userService.findAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.getFirst().companyId());
        assertEquals("USER", result.getFirst().role());
        assertNotNull(result.getFirst().id());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return user by ID")
    void findByIdShouldReturnUserDto() {
        User user = buildUser(1L, "Vitor", 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RegisterResponseDTO result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Vitor", result.name());
        assertEquals("USER", result.role());
    }

    @Test
    @DisplayName("Should throw EntityNotFound when user ID does not exist")
    void findByIdShouldThrowWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFound.class, () -> userService.findById(99L));
    }

    @Test
    @DisplayName("Should update user successfully when ID exists")
    void updateShouldReturnUpdatedUserDtoWhenIdExists() {
        Long userId = 1L;
        Long companyId = 10L;
        UpdateUserDTO dto = new UpdateUserDTO("Vitor Gonzalez", null, "11999999999", companyId);

        User existingUser = buildUser(userId, "Vitor", companyId);
        Company company = new Company();
        company.setId(companyId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(companyService.findById(companyId)).thenReturn(company);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterResponseDTO result = userService.update(userId, dto);

        assertNotNull(result);
        assertEquals("Vitor Gonzalez", existingUser.getName());
        assertEquals(company, existingUser.getCompany());
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Should throw EntityNotFound when updating non-existent user")
    void updateShouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;
        UpdateUserDTO dto = new UpdateUserDTO("Name", null, "123", 10L);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFound.class, () -> userService.update(userId, dto));
        verify(userRepository, never()).save(any());
        verify(companyService, never()).findById(any());
    }

    @Test
    @DisplayName("Should delete user and return DTO when ID exists")
    void deleteShouldRemoveUserAndReturnDto() {
        User user = buildUser(1L, "Vitor", 10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        RegisterResponseDTO result = userService.delete(1L);

        assertNotNull(result);
        assertEquals(10L, result.companyId());
        verify(userRepository, times(1)).delete(user);
    }
}

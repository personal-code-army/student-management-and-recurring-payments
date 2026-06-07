package br.com.geloteam.studentmanagement.application.user;

import br.com.geloteam.studentmanagement.domain.user.entity.Company;
import br.com.geloteam.studentmanagement.domain.user.entity.User;
import br.com.geloteam.studentmanagement.domain.user.port.out.CompanyRepositoryPort;
import br.com.geloteam.studentmanagement.domain.user.port.out.UserRepositoryPort;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import br.com.geloteam.studentmanagement.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private CompanyRepositoryPort companyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserUseCaseImpl userUseCaseImpl;

    private Company buildCompany(Long id) {
        Company company = new Company();
        company.setId(id);
        return company;
    }

    private User buildUser(Long id, Long companyId) {
        User user = new User();
        user.setId(id);
        user.setName("Vitor");
        user.setEmail("vitor@email.com");
        user.setCompanyId(companyId);
        return user;
    }

    @Test
    @DisplayName("Should return all users")
    void findAllShouldReturnUserList() {
        User user1 = buildUser(1L, 1L);
        User user2 = buildUser(2L, 1L);
        user2.setName("John");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userUseCaseImpl.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Vitor", result.get(0).getName());
        assertEquals("John", result.get(1).getName());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should return user by id")
    void findByIdShouldReturnUser() {
        User user = buildUser(1L, 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userUseCaseImpl.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when user not found by id")
    void findByIdShouldThrowNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userUseCaseImpl.findById(99L));
    }

    @Test
    @DisplayName("Should update user successfully when ID exists")
    void updateShouldSucceedWhenUserExists() {
        Long userId = 1L;
        Long companyId = 10L;
        Company company = buildCompany(companyId);
        User existingUser = buildUser(userId, 1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userUseCaseImpl.execute(userId, "Vitor Gonzalez", null, "11999999999", companyId);

        assertNotNull(result);
        assertEquals("Vitor Gonzalez", result.getName());
        assertEquals("11999999999", result.getCellphoneNumber());
        assertEquals(companyId, result.getCompanyId());
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent user")
    void updateShouldThrowNotFoundWhenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userUseCaseImpl.execute(99L, "Name", null, "123", 10L));

        verify(userRepository, never()).save(any());
        verify(companyRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should delete user and return deleted user when ID exists")
    void deleteShouldRemoveUserAndReturnIt() {
        User user = buildUser(1L, 10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userUseCaseImpl.execute(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(10L, result.getCompanyId());
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Should throw NotFoundException when deleting non-existent user")
    void deleteShouldThrowNotFoundWhenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userUseCaseImpl.execute(99L));
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should change password successfully")
    void changePasswordShouldSucceed() {
        User user = buildUser(1L, 1L);
        user.setPassword("encoded_old");

        when(userRepository.findByEmail("vitor@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "encoded_old")).thenReturn(true);
        when(passwordEncoder.encode("newPass@123")).thenReturn("encoded_new");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> userUseCaseImpl.execute("vitor@email.com", "oldPass", "newPass@123"));
        verify(passwordEncoder).encode("newPass@123");
        verify(userRepository).save(argThat(u -> u.getPassword().equals("encoded_new")));
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when current password is wrong")
    void changePasswordShouldFailOnWrongCurrentPassword() {
        User user = buildUser(1L, 1L);
        user.setPassword("encoded_old");

        when(userRepository.findByEmail("vitor@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encoded_old")).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> userUseCaseImpl.execute("vitor@email.com", "wrongPass", "newPass@123"));
        verify(userRepository, never()).save(any());
    }
}

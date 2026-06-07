package br.com.geloteam.studentmanagement.infrastructure.web.user;

import br.com.geloteam.studentmanagement.domain.user.entity.User;
import br.com.geloteam.studentmanagement.domain.user.port.in.ChangePasswordUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.DeleteUserUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.FindUserByIdUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.FindUserUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.UpdateUserUseCase;
import br.com.geloteam.studentmanagement.infrastructure.web.user.dto.ChangePasswordRequest;
import br.com.geloteam.studentmanagement.infrastructure.web.user.dto.UpdateUserRequest;
import br.com.geloteam.studentmanagement.infrastructure.web.user.dto.UserResponse;
import br.com.geloteam.studentmanagement.shared.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final FindUserUseCase findUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    public UserController(FindUserUseCase findUserUseCase,
                          FindUserByIdUseCase findUserByIdUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          DeleteUserUseCase deleteUserUseCase,
                          ChangePasswordUseCase changePasswordUseCase) {
        this.findUserUseCase = findUserUseCase;
        this.findUserByIdUseCase = findUserByIdUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        List<UserResponse> users = findUserUseCase.findAll().stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.data(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        User user = findUserByIdUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.data(UserResponse.from(user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable Long id,
                                                            @RequestBody @Valid UpdateUserRequest request) {
        User updated = updateUserUseCase.execute(id, request.name(), request.email(), request.cellphoneNumber(), request.companyId());
        return ResponseEntity.ok(ApiResponse.data(UserResponse.from(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> delete(@PathVariable Long id) {
        User deleted = deleteUserUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.data(UserResponse.from(deleted)));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestBody @Valid ChangePasswordRequest request,
                                                             Authentication authentication) {
        changePasswordUseCase.execute(authentication.getName(), request.currentPassword(), request.newPassword());
        return ResponseEntity.ok(ApiResponse.data(null));
    }
}

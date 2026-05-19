package br.com.geloteam.studentmanagement.infrastructure.web.user;

import br.com.geloteam.studentmanagement.domain.user.entity.User;
import br.com.geloteam.studentmanagement.domain.user.port.in.DeleteUserUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.FindUserUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.UpdateUserUseCase;
import br.com.geloteam.studentmanagement.infrastructure.web.user.dto.UpdateUserRequest;
import br.com.geloteam.studentmanagement.infrastructure.web.user.dto.UserResponse;
import br.com.geloteam.studentmanagement.shared.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final FindUserUseCase findUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    public UserController(FindUserUseCase findUserUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          DeleteUserUseCase deleteUserUseCase) {
        this.findUserUseCase = findUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        List<UserResponse> users = findUserUseCase.findAll().stream()
                .map(UserResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.data(users));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable Long id,
                                                            @RequestBody @Valid UpdateUserRequest request) {
        User updated = updateUserUseCase.execute(id, request.name(), request.cellphoneNumber(), request.companyId());
        return ResponseEntity.ok(ApiResponse.data(UserResponse.from(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> delete(@PathVariable Long id) {
        User deleted = deleteUserUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.data(UserResponse.from(deleted)));
    }
}

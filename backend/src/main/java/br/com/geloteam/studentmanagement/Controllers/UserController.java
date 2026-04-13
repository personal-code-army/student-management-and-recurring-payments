package br.com.geloteam.studentmanagement.Controllers;

import br.com.geloteam.studentmanagement.DTO.UpdateUserDTO;
import br.com.geloteam.studentmanagement.DTO.auth.RegisterResponseDTO;
import br.com.geloteam.studentmanagement.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<List<RegisterResponseDTO>> getAll() {
        List<RegisterResponseDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegisterResponseDTO> update(@PathVariable Long id, @RequestBody @Valid UpdateUserDTO data) {
        RegisterResponseDTO updatedUser = userService.update(id, data);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RegisterResponseDTO> delete(@PathVariable Long id) {
        RegisterResponseDTO deletedUser = userService.delete(id);
        return ResponseEntity.ok(deletedUser);
    }
}

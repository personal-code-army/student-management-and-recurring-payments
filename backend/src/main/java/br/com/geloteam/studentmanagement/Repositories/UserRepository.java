package br.com.geloteam.studentmanagement.Repositories;

import br.com.geloteam.studentmanagement.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

package br.com.geloteam.studentmanagement.domain.user.port.in;

import br.com.geloteam.studentmanagement.domain.user.entity.User;

import java.util.List;

public interface FindUserUseCase {
    List<User> findAll();
}

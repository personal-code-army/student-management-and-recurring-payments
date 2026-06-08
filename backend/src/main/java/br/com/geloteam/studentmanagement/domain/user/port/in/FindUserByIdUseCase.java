package br.com.geloteam.studentmanagement.domain.user.port.in;

import br.com.geloteam.studentmanagement.domain.user.entity.User;

public interface FindUserByIdUseCase {
    User findById(Long id);
}

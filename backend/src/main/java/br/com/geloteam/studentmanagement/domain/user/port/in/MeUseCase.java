package br.com.geloteam.studentmanagement.domain.user.port.in;

import br.com.geloteam.studentmanagement.domain.user.entity.User;

public interface MeUseCase {
    User execute(String email);
}

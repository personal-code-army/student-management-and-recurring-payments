package br.com.geloteam.studentmanagement.domain.user.port.in;

import br.com.geloteam.studentmanagement.application.user.AuthToken;

public interface LoginUseCase {
    AuthToken execute(String email, String password);
}

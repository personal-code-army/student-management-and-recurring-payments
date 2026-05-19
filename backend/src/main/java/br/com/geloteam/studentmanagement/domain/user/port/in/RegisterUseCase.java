package br.com.geloteam.studentmanagement.domain.user.port.in;

import br.com.geloteam.studentmanagement.domain.user.entity.User;

public interface RegisterUseCase {
    User execute(User user, String rawPassword, Long companyId);
}

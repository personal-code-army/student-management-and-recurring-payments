package br.com.geloteam.studentmanagement.domain.user.port.in;

import br.com.geloteam.studentmanagement.domain.user.entity.User;

public interface UpdateUserUseCase {
    User execute(Long id, String name, String cellphoneNumber, Long companyId);
}

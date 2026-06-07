package br.com.geloteam.studentmanagement.domain.user.port.out;

import br.com.geloteam.studentmanagement.domain.user.entity.Company;

import java.util.Optional;

public interface CompanyRepositoryPort {
    Optional<Company> findById(Long id);
}

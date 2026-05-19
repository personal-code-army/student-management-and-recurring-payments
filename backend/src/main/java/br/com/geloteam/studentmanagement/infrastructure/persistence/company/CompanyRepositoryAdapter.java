package br.com.geloteam.studentmanagement.infrastructure.persistence.company;

import br.com.geloteam.studentmanagement.domain.user.entity.Company;
import br.com.geloteam.studentmanagement.domain.user.port.out.CompanyRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CompanyRepositoryAdapter implements CompanyRepositoryPort {

    private final CompanyJpaRepository jpa;

    public CompanyRepositoryAdapter(CompanyJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Company> findById(Long id) {
        return jpa.findById(id);
    }
}

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
        return jpa.findById(id).map(this::toDomain);
    }

    private Company toDomain(CompanyJpaEntity e) {
        Company c = new Company();
        c.setId(e.getId());
        c.setCnpj(e.getCnpj());
        c.setName(e.getName());
        c.setCep(e.getCep());
        c.setAddress(e.getAddress());
        c.setCity(e.getCity());
        c.setCellphoneNumber(e.getCellphoneNumber());
        return c;
    }
}

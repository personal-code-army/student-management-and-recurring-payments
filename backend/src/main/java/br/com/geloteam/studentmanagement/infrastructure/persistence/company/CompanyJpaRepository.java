package br.com.geloteam.studentmanagement.infrastructure.persistence.company;

import br.com.geloteam.studentmanagement.domain.user.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyJpaRepository extends JpaRepository<Company, Long> {
}

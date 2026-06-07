package br.com.geloteam.studentmanagement.infrastructure.persistence.company;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyJpaRepository extends JpaRepository<CompanyJpaEntity, Long> {
}

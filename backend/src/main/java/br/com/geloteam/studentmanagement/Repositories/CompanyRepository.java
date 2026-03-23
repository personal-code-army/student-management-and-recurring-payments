package br.com.geloteam.studentmanagement.Repositories;

import br.com.geloteam.studentmanagement.Models.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}

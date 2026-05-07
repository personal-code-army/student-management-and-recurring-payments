package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.Models.Company;
import br.com.geloteam.studentmanagement.Repositories.CompanyRepository;
import br.com.geloteam.studentmanagement.exception.EntityNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Company não encontrada!"));
    }
}

package br.com.geloteam.studentmanagement.Repositories;

import br.com.geloteam.studentmanagement.Models.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
}

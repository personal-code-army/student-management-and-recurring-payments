package br.com.geloteam.studentmanagement.infrastructure.persistence.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StudentJpaRepository extends JpaRepository<StudentJpaEntity, Long> {

    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, Long id);
    List<StudentJpaEntity> findByActive(boolean active);

    @Query(value = "SELECT * FROM students WHERE unaccent(lower(name)) LIKE concat('%', unaccent(lower(:name)), '%')", nativeQuery = true)
    List<StudentJpaEntity> findByNameIgnoreCaseAndAccents(@Param("name") String name);

    @Query(value = "SELECT DISTINCT s.* FROM students s JOIN subscriptions sub ON sub.student_id = s.id JOIN payments p ON p.subscription_id = sub.id WHERE p.status = :status", nativeQuery = true)
    List<StudentJpaEntity> findByPaymentStatus(@Param("status") String status);

    @Query(value = "SELECT * FROM students WHERE EXTRACT(YEAR FROM AGE(birth_date)) BETWEEN :minAge AND :maxAge", nativeQuery = true)
    List<StudentJpaEntity> findByAgeRange(@Param("minAge") int minAge, @Param("maxAge") int maxAge);
}

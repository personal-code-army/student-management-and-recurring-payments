package br.com.geloteam.studentmanagement.infrastructure.web.student;

import br.com.geloteam.studentmanagement.domain.student.entity.Student;
import br.com.geloteam.studentmanagement.domain.student.port.in.CreateStudentUseCase;
import br.com.geloteam.studentmanagement.domain.student.port.in.DeleteStudentUseCase;
import br.com.geloteam.studentmanagement.domain.student.port.in.FindStudentUseCase;
import br.com.geloteam.studentmanagement.domain.student.port.in.UpdateStudentUseCase;
import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import br.com.geloteam.studentmanagement.infrastructure.web.student.dto.StudentRequest;
import br.com.geloteam.studentmanagement.infrastructure.web.student.dto.StudentResponse;
import br.com.geloteam.studentmanagement.shared.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final CreateStudentUseCase createStudentUseCase;
    private final UpdateStudentUseCase updateStudentUseCase;
    private final DeleteStudentUseCase deleteStudentUseCase;
    private final FindStudentUseCase findStudentUseCase;

    public StudentController(CreateStudentUseCase createStudentUseCase,
                             UpdateStudentUseCase updateStudentUseCase,
                             DeleteStudentUseCase deleteStudentUseCase,
                             FindStudentUseCase findStudentUseCase) {
        this.createStudentUseCase = createStudentUseCase;
        this.updateStudentUseCase = updateStudentUseCase;
        this.deleteStudentUseCase = deleteStudentUseCase;
        this.findStudentUseCase = findStudentUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> create(@RequestBody @Valid StudentRequest request) {
        Student student = toEntity(request);
        Student saved = createStudentUseCase.execute(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.data(StudentResponse.from(saved)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Boolean active
    ) {
        List<Student> students;

        if (name != null) {
            students = findStudentUseCase.findByName(name);
        } else if (paymentStatus != null) {
            students = findStudentUseCase.findByPaymentStatus(paymentStatus);
        } else if (minAge != null || maxAge != null) {
            students = findStudentUseCase.findByAgeRange(
                    minAge != null ? minAge : 0,
                    maxAge != null ? maxAge : 150
            );
        } else if (active != null) {
            students = findStudentUseCase.findByActive(active);
        } else {
            students = findStudentUseCase.findAll();
        }

        List<StudentResponse> response = students.stream().map(StudentResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.data(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getById(@PathVariable Long id) {
        Student student = findStudentUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.data(StudentResponse.from(student)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> update(@PathVariable Long id,
                                                               @RequestBody @Valid StudentRequest request) {
        Student student = toEntity(request);
        Student updated = updateStudentUseCase.execute(id, student);
        return ResponseEntity.ok(ApiResponse.data(StudentResponse.from(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        deleteStudentUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/{id}/subscriptions")
    public ResponseEntity<ApiResponse<List<Subscription>>> getSubscriptions(@PathVariable Long id) {
        List<Subscription> subscriptions = findStudentUseCase.findSubscriptionsByStudent(id);
        return ResponseEntity.ok(ApiResponse.data(subscriptions));
    }

    private Student toEntity(StudentRequest request) {
        Student student = new Student();
        student.setName(request.name());
        student.setCpf(request.cpf());
        student.setBirthDate(request.birthDate());
        student.setPhone(request.phone());
        student.setEmail(request.email());
        student.setAddress(request.address());
        student.setActive(request.active());
        student.setPlanId(request.planId());
        return student;
    }
}

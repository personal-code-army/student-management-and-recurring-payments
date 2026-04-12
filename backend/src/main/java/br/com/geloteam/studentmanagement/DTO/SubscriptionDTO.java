package br.com.geloteam.studentmanagement.DTO;

import br.com.geloteam.studentmanagement.Models.Plan;
import br.com.geloteam.studentmanagement.Models.Student;
import br.com.geloteam.studentmanagement.Services.PlanService;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public record SubscriptionDTO(
        @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate startDate,
        @NotBlank(message = "Status não informado") String status,
        @NotBlank(message = "Forma de pagamento não informada") String paymentMethod,
        @NotNull(message = "Plano não informado") Long planId,
        @NotNull(message = "Aluno não informado") Long studentId
) {

    public Long getPlanId(){
        return planId;
    }

    public Long getStudentId(){
        return studentId;
    }

}

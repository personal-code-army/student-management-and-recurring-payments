package br.com.geloteam.studentmanagement.DTO;

import br.com.geloteam.studentmanagement.Models.Plan;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SubscriptionDTO(
        @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate dueDate,
        @NotBlank(message = "Status não informado") String status,
        @NotBlank(message = "Forma de pagamento não informada") String paymentMethod,
        @NotNull Plan plan
        //Student student
) {

    public Plan getPlan(){
        return plan;
    }

//    public Student getStudent(){
//        return student;
//    }

}

package br.com.geloteam.studentmanagement.DTO;

import br.com.geloteam.studentmanagement.Models.Subscription;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PaymentDTO(
        @NotBlank(message = "Descrição não informada") String description,
        @NotNull(message = "Valor não informado") Double value,
        @NotBlank(message = "Forma de pagamento não informada") String paymentMethod,
        @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate dueDate,
        @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate paymentDate,
        String status,
        Subscription subscription
) {

    public Subscription getSubscription(){
        return subscription;
    }

}

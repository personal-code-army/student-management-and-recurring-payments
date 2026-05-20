package br.com.geloteam.studentmanagement.domain.payment.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor
public class Payment {
    private Long id;
    private Long subscriptionId;
    private String description;
    private double value;
    private String paymentMethod;
    private LocalDate dueDate;
    private LocalDate issueDate;
    private String status;
    private String mercadoPagoPreferenceId;
    private String mercadoPagoPaymentId;
    private String checkoutUrl;
    private String externalReference;
    private String payerName;
    private String payerEmail;
}

package br.com.geloteam.studentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StudentManagementAndRecurringPaymentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentManagementAndRecurringPaymentsApplication.class, args);
	}

}

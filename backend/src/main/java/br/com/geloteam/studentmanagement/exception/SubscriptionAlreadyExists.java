package br.com.geloteam.studentmanagement.exception;

public class SubscriptionAlreadyExists extends RuntimeException {
    public SubscriptionAlreadyExists(String message) {
        super(message);
    }
}

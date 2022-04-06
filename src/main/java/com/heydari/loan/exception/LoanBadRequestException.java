package com.heydari.loan.exception;

public class LoanBadRequestException extends RuntimeException{
    public LoanBadRequestException() {
        super();
    }
    public LoanBadRequestException(String message) {
        super(message);
    }
    public LoanBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

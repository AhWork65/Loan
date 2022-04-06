package com.heydari.loan.exception;

public class LoanCreateException extends Exception{
    public LoanCreateException() {
        super();
    }
    public LoanCreateException(String message) {
        super(message);
    }

    public LoanCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}

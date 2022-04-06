package com.heydari.loan.exception;

public class LoanInternalException extends  Exception{
    public LoanInternalException() {
        super();
    }
    public LoanInternalException(String message) {
        super(message);
    }

    public LoanInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}

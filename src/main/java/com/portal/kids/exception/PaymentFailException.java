package com.portal.kids.exception;

public class PaymentFailException extends RuntimeException {

    public PaymentFailException() {

    }

    public PaymentFailException(String message) {

        super(message);
    }
}

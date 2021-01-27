package com.uber.uberapi.exceptions;

public class InvalidBookingException extends UberException{

    public InvalidBookingException(String message) {
        super(message);
    }

    public static class InvalidPassengerException extends UberException {

        public InvalidPassengerException(String message) {
            super(message);
        }
    }
}

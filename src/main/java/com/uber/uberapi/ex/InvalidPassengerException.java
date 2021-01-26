package com.uber.uberapi.ex;

import com.uber.uberapi.exceptions.UberException;

public class InvalidPassengerException extends UberException {

    public InvalidPassengerException(String message) {
        super(message);
    }
}

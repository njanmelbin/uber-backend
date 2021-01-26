package com.uber.uberapi.services;

import com.uber.uberapi.models.Booking;
import com.uber.uberapi.models.Driver;
import com.uber.uberapi.models.Passenger;
import org.springframework.stereotype.Service;

public interface DriverMatchingService {
    void acceptBooking(Driver driver, Booking booking);
    void cancelByDriver(Driver driver, Booking booking);
    void cancelByPassenger(Passenger passenger, Booking booking);

    void assignDriver(Booking booking);
}

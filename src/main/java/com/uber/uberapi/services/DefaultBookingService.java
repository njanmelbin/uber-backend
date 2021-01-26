package com.uber.uberapi.services;

import com.uber.uberapi.models.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class DefaultBookingService implements BookingService{

    @Autowired
    DriverMatchingService driverMatchingService;

    @Autowired
    SchedulingService schedulingService;

    @Autowired
    OTPService otpService;
    @Override
    public void createBooking(Booking booking) {
        if(booking.getStartTime().after(new Date())){
            booking.setBookingStatus(BookingStatus.SCHEDULED);
            schedulingService.schedule(booking);

        }else{
            booking.setBookingStatus(BookingStatus.ASSIGNING_DRIVER);
            // ideally push it ot a queue and consume from it
            otpService.rideStartOTP(booking.getRideStartOTP());
            driverMatchingService.assignDriver(booking);
        }
    }

    @Override
    public void acceptBooking(Driver driver, Booking booking) {

    }

    @Override
    public void cancelByDriver(Driver driver, Booking booking) {

    }

    @Override
    public void cancelByPassenger(Passenger passenger, Booking booking) {

    }
}

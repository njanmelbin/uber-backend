package com.uber.uberapi.models;

public enum BookingStatus {
    CANCELLED("The booking has been cancelled"),
    SCHEDULED( "The booking is scheduled for some time in future"),
    ASSIGNING_DRIVER("The passenger has requested a booking ,driver is yet to be assigned"),
    REACHING_PICKUP_LOCATION("The driver is on the way"),
    CAB_ARRIVED("The driver has arrived at the pickup location and is waiting for passenger"),
    IN_RIDE("The ride is currently in progress"),
    COMPLETED("The ride has already been completed");

    String description;

    BookingStatus( String description){
        this.description = description;

    }
}

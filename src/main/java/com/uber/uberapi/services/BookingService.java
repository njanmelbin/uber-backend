package com.uber.uberapi.services;

import com.uber.uberapi.exceptions.InvalidActionForBookingStateException;
import com.uber.uberapi.models.*;
import com.uber.uberapi.repositories.BookingRepository;
import com.uber.uberapi.repositories.DriverRepository;
import com.uber.uberapi.repositories.PassengerRepository;
import com.uber.uberapi.services.drivermatching.DriverMatchingService;
import com.uber.uberapi.services.messagequeue.MessageQueue;
import com.uber.uberapi.services.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BookingService{

    @Autowired
    DriverMatchingService driverMatchingService;

    @Autowired
    SchedulingService schedulingService;

    @Autowired
    OTPService otpService;

    @Autowired
    MessageQueue messageQueue;

    @Autowired
    Constants constants;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    NotificationService notificationService;

    public void createBooking(Booking booking) {
        if(booking.getStartTime().after(new Date())){
            booking.setBookingStatus(BookingStatus.SCHEDULED);

            //producer
            messageQueue.sendMessage(constants.getSchedulingTopicName(),new SchedulingService.Message(booking));
            schedulingService.schedule(booking);

        }else{
            booking.setBookingStatus(BookingStatus.ASSIGNING_DRIVER);
            // ideally push it ot a queue and consume from it
            otpService.rideStartOTP(booking.getRideStartOTP());
           // driverMatchingService.assignDriver(booking);
            messageQueue.sendMessage (constants.getDriverMatchingTopicName(), new DriverMatchingService.Message(booking));
        }
        bookingRepository.save(booking);
        passengerRepository.save(booking.getPassenger());
    }


    public void acceptBooking(Driver driver, Booking booking) {
        if(!booking.needsDriver()){
            return;
        }
        // if driver is unavailable
        if(!driver.canAcceptBooking(constants.getMaxWaitTimeForPreviousTime())){
            notificationService.notify(driver.getPhoneNumber(), "Cannot accept Booking");
            return;
        }
        booking.setDriver(driver);
        driver.setActiveBooking(booking);
        booking.getNotifiedDrivers().clear();
        driver.getAcceptableBookings().clear();

        notificationService.notify(booking.getPassenger().getPhoneNumber(),driver.getName()+ " is arriving at pickup location");
        notificationService.notify(booking.getDriver().getPhoneNumber(),"Booking accepted");
        bookingRepository.save(booking);
        driverRepository.save(driver);
    }

    public void cancelByDriver(Driver driver, Booking booking) {
        booking.setDriver(null);
        driver.setActiveBooking(null);
        driver.getAcceptableBookings().remove(booking);
        notificationService.notify(booking.getPassenger().getPhoneNumber(),
                "Assigning Driver");
        notificationService.notify(booking.getDriver().getPhoneNumber(),
                "Booking has been cancelled");
        retryBooking(booking);
    }

    public void cancelByPassenger(Passenger passenger, Booking booking) {

        try{
            booking.cancel();
            bookingRepository.save(booking);

        }catch(InvalidActionForBookingStateException inner){
            notificationService.notify(booking.getPassenger().getPhoneNumber(),
                    "Cannot cancel the booking now if the ride is in progess, ask driver to end ride");
            throw inner;
        }

    }

    public void updateBooking(Booking booking, List<ExactLocation> route) {
        if(!booking.canChangeRoute()){
            throw new InvalidActionForBookingStateException("Ride has already been completed/cancelled");

        }
        booking.setRoute(route);
        bookingRepository.save(booking);
        notificationService.notify(booking.getDriver().getPhoneNumber(), "Route has been updated");
    }

    public void retryBooking(Booking booking) {
        createBooking(booking);
    }
}

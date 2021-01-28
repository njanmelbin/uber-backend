package com.uber.uberapi.controllers;

import com.uber.uberapi.exceptions.InvalidBookingException;
import com.uber.uberapi.exceptions.InvalidDriverException;
import com.uber.uberapi.models.Booking;
import com.uber.uberapi.models.Driver;
import com.uber.uberapi.models.OTP;
import com.uber.uberapi.models.Review;
import com.uber.uberapi.repositories.BookingRepository;
import com.uber.uberapi.repositories.DriverRepository;
import com.uber.uberapi.repositories.ReviewRepository;
import com.uber.uberapi.services.BookingService;
import com.uber.uberapi.services.Constants;
import com.uber.uberapi.services.drivermatching.DriverMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    DriverMatchingService driverMatchingService;

    @Autowired
    BookingService bookingService;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    Constants constants;

    public Driver getDriverFromId(Long driverId){
        Optional<Driver> driver =  driverRepository.findById(driverId);
        if(driver.isEmpty()){
            throw new InvalidBookingException("No driver with id "+ driverId);
        }
        return driver.get();
    }

    private Booking getBookingFromId(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if(booking.isEmpty()){
            throw new InvalidDriverException("No booking with id" + bookingId);
        }

        return booking.get();
    }

    public Booking getDriverBookingFromId(Long bookingId , Driver driver){
        Booking booking = getBookingFromId(bookingId);
        if(!booking.getDriver().equals(driver)){
            throw new InvalidBookingException("Driver with" + driver.getId() +" has no such booking");
        }
        return booking;
    }
    @GetMapping("/{driverId}")
    public Driver getDriverDetails(@PathVariable(name = "driverId") Long driverId){
        Driver driver = getDriverFromId(driverId);
        return driver;
    }

    @PutMapping("/{driverId}")
    public void changeAvailability(@PathVariable(name ="driverId")Long driverId,@RequestBody Boolean available){
        Driver driver = getDriverFromId(driverId);
        driver.setIsAvailable(available);
        driverRepository.save(driver);
    }

    @GetMapping("/{driverId}/bookings")
    public List<Booking> getAllBookings(@PathVariable(name ="driverId")Long driverId){
        Driver driver = getDriverFromId(driverId);
        return driver.getBookings();
    }

    @GetMapping("/{driverId}/bookings/{bookingId}")
    public Booking getBooking(@PathVariable(name ="driverId")Long driverId,
                              @PathVariable(name ="bookingId")Long bookingId){
        Driver driver = getDriverFromId(driverId);

        Booking booking = getDriverBookingFromId(bookingId,driver);
        return booking;
    }

    @PostMapping("/{driverId}/bookings/{bookingId}")
    public void acceptBooking(@PathVariable(name="driverId")Long driverId,
                              @PathVariable(name="bookingId")Long bookingId){
        Driver driver = getDriverFromId(driverId);

        Booking booking = getBookingFromId(bookingId);
        bookingService.acceptBooking(driver,booking);
    }



    @DeleteMapping("/{driverId}/bookings/{bookingId}")
    public void cancelBooking(@PathVariable(name="driverId")Long driverId,
                              @PathVariable(name="bookingId")Long bookingId){
        Driver driver = getDriverFromId(driverId);

        Booking booking = getDriverBookingFromId(bookingId,driver);
        bookingService.cancelByDriver(driver,booking);

    }

    // rate the booking
    // start the rode
    // stop the rideå
    @PatchMapping("/{driverId}/bookings/{bookingId}/start")
    public void startRide(@PathVariable(name="driverId")Long driverId,
                              @PathVariable(name="bookingId")Long bookingId,
                                @RequestBody OTP otp){
        Driver driver = getDriverFromId(driverId);

        Booking booking = getDriverBookingFromId(bookingId,driver);

        booking.startRide(otp,constants.getRideStartOTPExpiryMinutes());
        bookingRepository.save(booking);

    }

    @PatchMapping("/{driverId}/bookings/{bookingId}/end")
    public void endRide(@PathVariable(name="driverId")Long driverId,
                          @PathVariable(name="bookingId")Long bookingId ){
        Driver driver = getDriverFromId(driverId);

        Booking booking = getDriverBookingFromId(bookingId,driver);

        booking.endRide();
        driverRepository.save(driver);
        bookingRepository.save(booking);

    }

    @PatchMapping("/{driverId}/bookings/{bookingId}/rate")
    public void rateRide(@PathVariable(name="driverId")Long driverId,
                        @PathVariable(name="bookingId")Long bookingId ,
                        @RequestBody Review data){
        Driver driver = getDriverFromId(driverId);

        Booking booking = getDriverBookingFromId(bookingId,driver);
        Review review = Review.builder()
                .note(data.getNote())
                .ratingOutOfFive(data.getRatingOutOfFive())
                .build();
        booking.setReviewByDriver(review);
        reviewRepository.save(review);
        bookingRepository.save(booking);
    }
}

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
import com.uber.uberapi.services.DriverMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

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

    public Driver getDriverFromId(Long driverId){
        Optional<Driver> driver =  driverRepository.findById(driverId);
        if(driver.isEmpty()){
            throw new InvalidBookingException("No driver with id "+ driverId);
        }
        return driver.get();
    }


    public Booking getDriverBookingFromId(Long bookingId , Driver driver){
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if(optionalBooking.isEmpty()){
            throw new InvalidDriverException("No booking with id" + bookingId);
        }
        Booking booking =  optionalBooking.get();
        if(!booking.getDriver().equals(driver)){
            throw new InvalidBookingException("Driver with" + driver.getId() +" has no such booking");
        }
        return booking;
    }
    @GetMapping("/{driverId}")
    public Driver getDriverDetails(@RequestParam(name = "driverId") Long driverId){
        Driver driver = getDriverFromId(driverId);
        return driver;
    }

    @PutMapping("/{driverId}")
    public void changeAvailability(@RequestParam(name ="driverId")Long driverId,@RequestBody Boolean available){
        Driver driver = getDriverFromId(driverId);
        driver.setIsAvailable(available);
        driverRepository.save(driver);
    }

    @GetMapping("/{driverId}/bookings")
    public List<Booking> getAllBookings(@RequestParam(name ="driverId")Long driverId){
        Driver driver = getDriverFromId(driverId);
        return driver.getBookings();
    }

    @GetMapping("/{driverId}/bookings/{bookingId}")
    public Booking getBooking(@RequestParam(name ="driverId")Long driverId,
                              @RequestParam(name ="bookingId")Long bookingId){
        Driver driver = getDriverFromId(driverId);

        Booking booking = getDriverBookingFromId(bookingId,driver);
        return booking;
    }

    @PostMapping("/{driverId}/bookings/{bookingId}")
    public void acceptBooking(@RequestParam(name="driverId")Long driverId,
                              @RequestParam(name="bookingId")Long bookingId){
        Driver driver = getDriverFromId(driverId);

        Booking booking = getDriverBookingFromId(bookingId,driver);
        bookingService.acceptBooking(driver,booking);
    }

    @DeleteMapping("/{driverId}/bookings/{bookingId}")
    public void cancelBooking(@RequestParam(name="driverId")Long driverId,
                              @RequestParam(name="bookingId")Long bookingId){
        Driver driver = getDriverFromId(driverId);

        Booking booking = getDriverBookingFromId(bookingId,driver);
        bookingService.cancelByDriver(driver,booking);

    }

    // rate the booking
    // start the rode
    // stop the rideå
    @PatchMapping("/{driverId}/bookings/{bookingId}/start")
    public void startRide(@RequestParam(name="driverId")Long driverId,
                              @RequestParam(name="bookingId")Long bookingId,
                                @RequestBody OTP otp){
        Driver driver = getDriverFromId(driverId);

        Booking booking = getDriverBookingFromId(bookingId,driver);

        booking.startRide(otp);
        bookingRepository.save(booking);

    }

    @PatchMapping("/{driverId}/bookings/{bookingId}/end")
    public void endRide(@RequestParam(name="driverId")Long driverId,
                          @RequestParam(name="bookingId")Long bookingId ){
        Driver driver = getDriverFromId(driverId);

        Booking booking = getDriverBookingFromId(bookingId,driver);

        booking.endRide();
        bookingRepository.save(booking);

    }

    @PatchMapping("/{driverId}/bookings/{bookingId}/rate")
    public void rateRide(@RequestParam(name="driverId")Long driverId,
                        @RequestParam(name="bookingId")Long bookingId ,
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

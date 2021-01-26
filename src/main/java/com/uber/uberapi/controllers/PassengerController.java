package com.uber.uberapi.controllers;

import com.uber.uberapi.ex.InvalidPassengerException;
import com.uber.uberapi.exceptions.InvalidBookingException;
import com.uber.uberapi.exceptions.InvalidDriverException;
import com.uber.uberapi.models.*;
import com.uber.uberapi.repositories.BookingRepository;
import com.uber.uberapi.repositories.DriverRepository;
import com.uber.uberapi.repositories.PassengerRepository;
import com.uber.uberapi.repositories.ReviewRepository;
import com.uber.uberapi.services.BookingService;
import com.uber.uberapi.services.DriverMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    BookingService bookingService;

    @Autowired
    ReviewRepository reviewRepository;

    public Passenger getPassengerFromId(Long passengerId){
        Optional<Passenger> passenger =  passengerRepository.findById(passengerId);
        if(passenger.isEmpty()){
            throw new InvalidPassengerException("No driver with id "+ passengerId);
        }
        return passenger.get();

    }


    public Booking getPassengerBookingFromId(Long bookingId , Passenger passenger){
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if(optionalBooking.isEmpty()){
            throw new InvalidPassengerException("No booking with id" + bookingId);
        }
        Booking booking =  optionalBooking.get();
        if(!booking.getDriver().equals(passenger)){
            throw new InvalidBookingException("Driver with" + passenger.getId() +" has no such booking");
        }
        return booking;
    }
    @GetMapping("/{passengerId}")
    public Passenger getDriverDetails(@RequestParam(name = "passengerId") Long passengerId){
        Passenger passenger = getPassengerFromId(passengerId);
        return passenger;
    }


    @GetMapping("/{passengerId}/bookings")
    public List<Booking> getAllBookings(@RequestParam(name ="passengerId")Long passengerId){
        Passenger passenger = getPassengerFromId(passengerId);
        return passenger.getBookings();
    }

    @GetMapping("/{passengerId}/bookings/{bookingId}")
    public Booking getBooking(@RequestParam(name ="passengerId")Long passengerId,
                              @RequestParam(name ="bookingId")Long bookingId){
        Passenger passenger = getPassengerFromId(passengerId);

        Booking booking = getPassengerBookingFromId(bookingId,passenger);
        return booking;
    }

    @PostMapping("/{passengerId}/bookings/")
    public void requestBooking(@RequestParam(name="passengerId")Long passengerId,
                              @RequestBody Booking data){
        Passenger passenger = getPassengerFromId(passengerId);
        Booking booking = Booking.builder()
                .build();
        bookingService.createBooking(booking);
        bookingRepository.save(booking);
        passengerRepository.save(passenger);
    }

    @DeleteMapping("/{passengerId}/bookings/{bookingId}")
    public void cancelBooking(@RequestParam(name="passengerId")Long passengerId,
                              @RequestParam(name="bookingId")Long bookingId){
        Passenger passenger = getPassengerFromId(passengerId);

        Booking booking = getPassengerBookingFromId(bookingId,passenger);
        bookingService.cancelByPassenger(passenger,booking);

    }

    // rate the booking
    // start the rode
    // stop the rideå



    @PatchMapping("/{passengerId}/bookings/{bookingId}/rate")
    public void rateRide(@RequestParam(name="passengerId")Long passengerId,
                        @RequestParam(name="bookingId")Long bookingId ,
                        @RequestBody Review data){
        Passenger passenger = getPassengerFromId(passengerId);

        Booking booking = getPassengerBookingFromId(bookingId,passenger);
        Review review = Review.builder()
                .note(data.getNote())
                .ratingOutOfFive(data.getRatingOutOfFive())
                .build();
        booking.setReviewByPassenger(review);
        reviewRepository.save(review);
        bookingRepository.save(booking);
    }
}

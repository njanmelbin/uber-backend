package com.uber.uberapi.controllers;

import com.uber.uberapi.exceptions.InvalidBookingException;
import com.uber.uberapi.models.*;
import com.uber.uberapi.repositories.BookingRepository;
import com.uber.uberapi.repositories.PassengerRepository;
import com.uber.uberapi.repositories.ReviewRepository;
import com.uber.uberapi.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
            throw new InvalidBookingException.InvalidPassengerException("No driver with id "+ passengerId);
        }
        return passenger.get();

    }


    public Booking getPassengerBookingFromId(Long bookingId , Passenger passenger){
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if(optionalBooking.isEmpty()){
            throw new InvalidBookingException.InvalidPassengerException("No booking with id" + bookingId);
        }
        Booking booking =  optionalBooking.get();
        if(!booking.getDriver().equals(passenger)){
            throw new InvalidBookingException("Driver with" + passenger.getId() +" has no such booking");
        }
        return booking;
    }
    @GetMapping("/{passengerId}")
    public Passenger getDriverDetails(@PathVariable(name = "passengerId") Long passengerId){
        Passenger passenger = getPassengerFromId(passengerId);
        return passenger;
    }


    @GetMapping("/{passengerId}/bookings")
    public List<Booking> getAllBookings(@PathVariable(name ="passengerId")Long passengerId){
        Passenger passenger = getPassengerFromId(passengerId);
        return passenger.getBookings();
    }

    @GetMapping("/{passengerId}/bookings/{bookingId}")
    public Booking getBooking(@PathVariable(name ="passengerId")Long passengerId,
                              @PathVariable(name ="bookingId")Long bookingId){
        Passenger passenger = getPassengerFromId(passengerId);

        Booking booking = getPassengerBookingFromId(bookingId,passenger);
        return booking;
    }

    @PostMapping("/{passengerId}/bookings/")
    public void requestBooking(@PathVariable(name="passengerId")Long passengerId,
                              @RequestBody Booking data){
        Passenger passenger = getPassengerFromId(passengerId);
        List<ExactLocation> route = new ArrayList<>();
        data.getRoute().forEach(exactLocation -> {
            route.add(ExactLocation.builder()
                    .latitude(exactLocation.getLatitude())
                    .longitude(exactLocation.getLongitude())
                    .build());
        });
        Booking booking = Booking.builder()
                .rideStartOTP(OTP.make(passenger.getPhoneNumber()))
                .route(route)
                .bookingType(data.getBookingType())
                .scheduledTime(data.getScheduledTime())
                .build();
        bookingService.createBooking(booking);
    }

    @DeleteMapping("/{passengerId}/bookings/{bookingId}")
    public void cancelBooking(@PathVariable(name="passengerId")Long passengerId,
                              @PathVariable(name="bookingId")Long bookingId){
        Passenger passenger = getPassengerFromId(passengerId);

        Booking booking = getPassengerBookingFromId(bookingId,passenger);
        bookingService.cancelByPassenger(passenger,booking);

    }

    // rate the booking
    // start the rode
    // stop the rideå

    @PatchMapping("{passengerId}/bookings/{bookingId}")
    public void updateRoute(@PathVariable(name="passengerId") Long passengerId,
                            @PathVariable(name="bookingId") Long bookingId,
                            @RequestBody Booking data){
        Passenger passenger = getPassengerFromId(passengerId);

        Booking booking = getPassengerBookingFromId(bookingId,passenger);
        List<ExactLocation> route = new ArrayList<>();
        data.getRoute().forEach(exactLocation -> {
            route.add(ExactLocation.builder()
                    .latitude(exactLocation.getLatitude())
                    .longitude(exactLocation.getLongitude())
                    .build());
        });

        bookingService.updateBooking(booking,route);

    }

    @PatchMapping("/{passengerId}/bookings/{bookingId}/rate")
    public void rateRide(@PathVariable(name="passengerId")Long passengerId,
                        @PathVariable(name="bookingId")Long bookingId ,
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

    @PostMapping("/{passengerId}/bookings/{bookingId}")
    public void retryBooking(@PathVariable(name="passengerId")Long passengerId,
                             @PathVariable(name="bookingId")Long bookingId    ){
        Passenger passenger = getPassengerFromId(passengerId);
        Booking booking = getPassengerBookingFromId(bookingId,passenger);

        bookingService.retryBooking(booking);
    }
}

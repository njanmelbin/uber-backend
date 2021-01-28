package com.uber.uberapi.controllers;

import com.uber.uberapi.exceptions.InvalidBookingException;
import com.uber.uberapi.models.Driver;
import com.uber.uberapi.models.ExactLocation;
import com.uber.uberapi.models.Passenger;
import com.uber.uberapi.repositories.DriverRepository;
import com.uber.uberapi.repositories.PassengerRepository;
import com.uber.uberapi.services.Constants;
import com.uber.uberapi.services.locationtracking.LocationTrackingService;
import com.uber.uberapi.services.messagequeue.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/location")
public class LocationTrackingController {

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    LocationTrackingService locationTrackingService;

    @Autowired
    MessageQueue messageQueue;

    @Autowired
    Constants constants;

    public Driver getDriverFromId(Long driverId){
        Optional<Driver> driver =  driverRepository.findById(driverId);
        if(driver.isEmpty()){
            throw new InvalidBookingException("No driver with id "+ driverId);
        }
        return driver.get();
    }

    public Passenger getPassengerFromId(Long passengerId){
        Optional<Passenger> passenger =  passengerRepository.findById(passengerId);
        if(passenger.isEmpty()){
            throw new InvalidBookingException.InvalidPassengerException("No driver with id "+ passengerId);
        }
        return passenger.get();

    }

    @PutMapping("driver/{driverId}")
    public void updateDriverLocation(@PathVariable Long driverId,
                                      @RequestBody ExactLocation data){
        // once every 3 seconds for every actual driver
        // todo: check if driver has an active booking
        //      update the bookings completedRoute based on drivers location
        //      update the completion time
        Driver driver = getDriverFromId(driverId);
        ExactLocation location = ExactLocation.builder()
                .longitude(data.getLongitude())
                .latitude(data.getLatitude())
                .build();
        messageQueue.sendMessage(constants.getLocationTrackingTopicName(),new LocationTrackingService.Message(driver,location));
        // delete the taks
    }

    @PutMapping("/passenger/{passengerId}")
    public void updatePassengerLocation(@PathVariable Long passengerId,
                                        @RequestBody ExactLocation location){
        // only triggers every 30 seconds if the passenger is active
        Passenger passenger = getPassengerFromId(passengerId);
        passenger.setLastKnownLocation(ExactLocation.builder()
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .build());

        passengerRepository.save(passenger);
    }
}

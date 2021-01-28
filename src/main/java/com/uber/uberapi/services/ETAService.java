package com.uber.uberapi.services;

import com.uber.uberapi.models.ExactLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ETAService {

    @Autowired
    Constants   constants;

    public int getETAInMinutes(ExactLocation lastKnownLocation, ExactLocation pickup) {
        return(int) (60 * lastKnownLocation.distanceKm(pickup)/ constants.getDefaultETASpeedKmph());
    }
}

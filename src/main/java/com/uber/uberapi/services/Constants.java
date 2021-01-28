package com.uber.uberapi.services;

import com.uber.uberapi.repositories.DBConstantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Constants {

    private final Integer TEN_MINUTES = 60*10*1000;

    final DBConstantRepository dbConstantRepository;

    private final Map<String,String> constants = new HashMap<>();


    public Constants(DBConstantRepository dbConstantRepository) {
        this.dbConstantRepository = dbConstantRepository;
        loadConstantsFromDB();
    }

    @Scheduled(fixedRate =TEN_MINUTES )
    private void loadConstantsFromDB() {
        dbConstantRepository.findAll().forEach( dbConstant -> {
            constants.put(dbConstant.getName(),dbConstant.getValue());
        });
    }

    public Integer getRideStartOTPExpiryMinutes(){
        return Integer.parseInt(constants.getOrDefault("rideStartOTPExpiryMinutes","3600000"));

    }


    public String getSchedulingTopicName() {
        return constants.getOrDefault("schedulingTopicName","schedulingServiceTopic");
    }

    public String getDriverMatchingTopicName() {
        return constants.getOrDefault("driverMatchingTopicName","driverMatchingTopic");
    }

    public Integer getMaxWaitTimeForPreviousTime() {
        return Integer.parseInt(constants.getOrDefault("maxWaitTimeForPreviousTime","900000"));
    }

    public Integer getProcessBookingBeforeTime() {
        return Integer.parseInt(constants.getOrDefault("processBookingBeforeTime","900000"));

    }

    public String getLocationTrackingTopicName() {
        return constants.getOrDefault("locationTrackingTopicName","locationTrackingTopic");
    }

    public  Double getMaxDistanceKmForDriverMatching() {
        return Double.parseDouble(constants.getOrDefault("maxDistanceKmForDriverMatching","2"));

    }

    public int getMaxDriverETAMinutes() {
        return Integer.parseInt(constants.getOrDefault("maxDriverETAMinutes","15"));
    }

    public boolean getIsETABasedFilterEnabled() {
        return Boolean.parseBoolean(constants.getOrDefault("isETABasedFilterEnable","true"));
    }

    public boolean getIsGenderBasedFilterEnabled() {
        return Boolean.parseBoolean(constants.getOrDefault("isETABasedFilterEnabled","true"));
    }

    public double getDefaultETASpeedKmph() {
        return Double.parseDouble(constants.getOrDefault("getDefaultETASpeedKmph","30.0"));

    }
}

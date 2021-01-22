package com.uber.uberapi.controllers;

import com.uber.uberapi.models.BookingType;
import com.uber.uberapi.models.ExactLocation;

public interface ETAStrategy {
    public Integer getETAInMinutes(ExactLocation start,ExactLocation end);

}


class CombinedPricingStrategy implements PricingStrategy{
    private static List<PriceDelta> priceDeltaList ;

    static{
        priceDeltaList.add(new WeatherPriceDelta());
        priceDeltaList.add(new BookingTypePriceDelta());

    }

    public Integer getPriceInRupees(Booking booking){
        Integer price =1;
        for(PriceDelta priceDelta:priceDeltaList){
            price = priceDelta.apply(booking,price);
        }
        return null;
    }
}

interface PriceDelta {
    public Integer apply(Booking booking,Integer price);
}


class BookingTypePriceDelta implements PriceDelta{

    @Override
    public Integer apply(Booking booking,Integer price) {
        if(booking.getBookingType().equals(BookingType.Prime));
            return price*2;

            return price;
    }
}

// similary other price delta based on weather
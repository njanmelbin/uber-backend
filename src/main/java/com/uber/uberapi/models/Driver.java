package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="driver" , indexes = {
        @Index( columnList = "account_id" , unique = true),
        @Index( columnList = "car_id" , unique = true),
})

public class Driver extends Auditable{

    private String picUrl;// image location - Amazon S3

    @OneToOne
    private Account user;

    private String name;
    private Gender gender;

    @OneToOne(mappedBy = "driver")
    private Car car;

    private String licenseDetails;

    @Temporal(value= TemporalType.DATE)
    private Date dob;

    @Enumerated(value = EnumType.STRING)
    private DriverApprovalStatus status;

    @OneToMany(mappedBy="driver")
    private List<Booking> bookings = new ArrayList<>();

    private Boolean isAvailable;

    private String activeCity;

    @OneToOne
    private ExactLocation lastKnownExactLocation;

    @OneToOne
    private ExactLocation home;
}

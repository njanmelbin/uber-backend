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
@Table(name="passenger")
public class Passenger extends Auditable{
    // database to provide the id for us
    // autoincrement
    @OneToOne(cascade = CascadeType.ALL)
    private Account user;

    private String name;

    @Enumerated(value=EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "passenger")
    private List<Booking> bookings = new ArrayList<>();

    @Temporal(TemporalType.DATE)
    private Date dob;

    private String phoneNumber;

    @OneToOne
    private ExactLocation home;

    @OneToOne
    private ExactLocation work;

    @OneToOne
    private ExactLocation lastKnownExactLocation;
}

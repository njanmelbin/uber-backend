package com.uber.uberapi.models;

import com.uber.uberapi.exceptions.UnapprovedDriverException;
import com.uber.uberapi.utils.DateUtils;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "driver")
public class Driver extends Auditable {
    @OneToOne
    private Account account;

    private Gender gender;

    private String name;

    @OneToOne(mappedBy = "driver")
    private Car car;

    private String licenseDetails;

    @Temporal(value = TemporalType.DATE)
    private Date dob;

    @Enumerated(value = EnumType.STRING)
    private DriverApprovalStatus approvalStatus;

    @OneToMany(mappedBy = "driver")
    private List<Booking> bookings;

    @ManyToMany(mappedBy = "notifieddrivers",cascade=CascadeType.PERSIST)
    private Set<Booking> acceptableBookings = new HashSet<>(); // bookings that driver can currently accept

    @OneToOne
    private Booking activeBooking=null;

    private Boolean isAvailable;

    private String activeCity;

    private String phoneNumber;

    @OneToOne
    private ExactLocation lastKnownLocation;

    @OneToOne
    private ExactLocation home;

    public void setAvailable(Boolean available) {
        if(available && !approvalStatus.equals(DriverApprovalStatus.APPROVED)){
            throw new UnapprovedDriverException("Driver approval pending or denied" + getId());
        }
        isAvailable = available;
    }

    public boolean canAcceptBooking(int maxWaitTimeForPreviousTime) {
        if(isAvailable && activeBooking == null){
            return true;
        }
        // check whether if teh current ride ends in 10 minutes i can accept
        return activeBooking.getExpectedCompletionTime().before(DateUtils.addMinutes(new Date(),maxWaitTimeForPreviousTime) );
    }

}

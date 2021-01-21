package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="namedlocation")
public class NameLocation extends Auditable {

    @OneToOne
    private ExactLocation exactLocation;

    private String name;

    private String city;

    private String zipCode;

    private String state;

    private String country;

}

// multiple entries in table with same latitude and longitude

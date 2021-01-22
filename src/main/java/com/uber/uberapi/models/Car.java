package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="car" , indexes = {
        @Index(columnList = "driver_id" , unique = true)
})

public class Car extends Auditable {

    @ManyToOne
    private Color color;

    private String plateNumber;

    private String brand;

    private String model;

    @Enumerated(value = EnumType.STRING)
    private CarType carType;

    @OneToOne
    private Driver driver;


}

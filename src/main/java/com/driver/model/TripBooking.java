package com.driver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TripBooking {
    private int tripBookingId;
    private String fromLocation;
    private String toLocation;

    private int distanceInKm;
    @Enumerated(EnumType.STRING)
    TripStatus Status;
    private int bill;

    @ManyToOne
    @JoinColumn
    Driver driver;

    @ManyToOne
    @JoinColumn
    Customer customer;

}

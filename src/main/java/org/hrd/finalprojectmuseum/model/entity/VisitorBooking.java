package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class VisitorBooking {
    private UUID visitorId;
    private String fullName;
    private String email;
    private Integer totalBookings;
    private Integer totalTickets;
}

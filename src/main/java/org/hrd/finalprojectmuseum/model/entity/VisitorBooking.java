package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;

@Data
public class VisitorBooking {
    private String fullName;
    private String email;
    private Integer totalBookings;
    private Integer totalTickets;
}

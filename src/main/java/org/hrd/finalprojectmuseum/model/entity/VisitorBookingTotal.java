package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;

@Data
public class VisitorBookingTotal {
    private Integer totalTicket;
    private double compareLastMonthTicket;
    private Integer totalBooking;
    private double compareLastMonthBooking;
}

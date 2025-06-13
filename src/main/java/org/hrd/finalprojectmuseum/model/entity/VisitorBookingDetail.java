package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VisitorBookingDetail {
    private String museumName;
    private String ticketType;
    private String bookingType;
    private LocalDate bookingDate;
}

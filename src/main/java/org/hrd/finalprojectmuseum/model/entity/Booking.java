package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumShortInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class Booking {
    private UUID bookingId;
    private MuseumShortInfo museum;
    private UUID visitorId;
    private BigDecimal ticketPrice;
    private String ticketType;
    private String ticketStatus;
    private String bookingType;
    private Integer slotAmount;
    private LocalDateTime bookingDate;
    private LocalDateTime expiryDate;
    private String qrCode;
    private BigDecimal totalPrice;
    private LocalDateTime createAt;
}

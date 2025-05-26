package org.hrd.finalprojectmuseum.model.dto.response;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumShortInfo;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingManagement {
    private UUID bookingId;
    private UUID museumId;
    private Visitor visitor;
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

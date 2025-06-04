package org.hrd.finalprojectmuseum.model.entity.visitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.enums.TicketStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingV2 {
    private UUID bookingId;
    private String museumName;
    private String visitorName;
    private String bookingType;
    private LocalDateTime bookingDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ticketType;
    private LocalDateTime purchasedDate;
    private Double ticketPrice;
    private Integer slotAmount;
    private String qrCode;
    private String ticketStatus;
}

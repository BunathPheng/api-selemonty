package org.hrd.finalprojectmuseum.model.entity.visitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.enums.TicketStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingV2 {
    private UUID bookingId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String museumName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String visitorName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bookingType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime bookingDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ticketType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime purchasedDate;
    private Double ticketPrice;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer slotAmount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String qrCode;
    private String ticketStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime expiredDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String museumLogo;
}

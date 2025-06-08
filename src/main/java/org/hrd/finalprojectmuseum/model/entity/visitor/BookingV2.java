package org.hrd.finalprojectmuseum.model.entity.visitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingV2 {
    private UUID bookingId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID museumId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String museumName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID visitorId;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal ticketPrice;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer slotAmount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal totalPrice;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String qrCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String ticketStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime expiredDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String museumLogo;
}

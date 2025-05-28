package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.model.enums.TourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class Tour {
    private UUID tourId;
    private List<Guide> guides;
    private Visitor visitor;
    private Integer slotAmount;
    private LocalDateTime bookingDate;
    private LocalDateTime expiryDate;
    private String ticketStatus;
    private BigDecimal tourPrice;
    private TourStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumShortInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TicketInfo {
    private UUID ticketInfoId;
    private MuseumShortInfo museum;
    private BigDecimal localPrice;
    private BigDecimal foreignPrice;
    private Integer totalSlot;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

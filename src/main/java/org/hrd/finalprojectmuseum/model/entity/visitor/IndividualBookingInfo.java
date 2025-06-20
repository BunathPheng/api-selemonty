package org.hrd.finalprojectmuseum.model.entity.visitor;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class IndividualBookingInfo {
    private UUID museumId;
    private UUID ticketId;
    private BigDecimal localPrice;
    private BigDecimal foreignPrice;
    private Integer totalSlots;
    private List<MuseumSchedule> museumSchedule;
}

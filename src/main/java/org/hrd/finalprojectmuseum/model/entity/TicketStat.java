package org.hrd.finalprojectmuseum.model.entity;

import lombok.Builder;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.dto.response.StatItem;

@Data
@Builder
public class TicketStat {
    private StatItem totalSales;
    private StatItem totalTicket;
}

package org.hrd.finalprojectmuseum.model.entity;

import lombok.Builder;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.dto.response.StatItem;

@Builder
@Data
public class VisitorStat {
    private StatItem totalVisitors;
    private StatItem newVisitors;
    private StatItem newBooking;
}

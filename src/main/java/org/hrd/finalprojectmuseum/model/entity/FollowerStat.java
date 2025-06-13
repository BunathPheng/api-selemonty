package org.hrd.finalprojectmuseum.model.entity;

import lombok.Builder;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.dto.response.StatItem;

@Data
@Builder
public class FollowerStat {
    private StatItem totalFollowers;
    private StatItem newFollowers;
    private StatItem newBooking;
}

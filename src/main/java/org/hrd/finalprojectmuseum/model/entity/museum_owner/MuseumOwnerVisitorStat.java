package org.hrd.finalprojectmuseum.model.entity.museum_owner;

import lombok.Builder;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.dto.response.StatItem;

@Data
@Builder
public class MuseumOwnerVisitorStat {
    private StatItem totalVisitors;
    private StatItem newVisitors;
}

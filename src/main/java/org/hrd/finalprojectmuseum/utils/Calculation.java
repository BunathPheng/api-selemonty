package org.hrd.finalprojectmuseum.utils;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hrd.finalprojectmuseum.model.dto.response.StatItem;

@NoArgsConstructor
public class Calculation {
    public StatItem addStatItem(Integer nowItem, Integer lastItem){
        return StatItem.builder()
                .value(nowItem)
                .percentageChange(calculatePercentageChange(nowItem, lastItem))
                .build();
    }

    public Double calculatePercentageChange(Integer current, Integer previous) {
        if (previous == null || previous == 0) {
            if (current == null || current == 0) {
                return 0.0;
            } else {
                return 100.0;
            }
        }

        if (current == null) current = 0;

        return ((double) (current - previous) / previous) * 100;
    }
}

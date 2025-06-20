package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.dto.response.BookingStats;

@Data
public class BookingAnalytics {
    private BookingStats localVisitors;
    private BookingStats foreignerVisitors;
    private BookingStats tourVisitors;
    private Integer totalLocals;
    private Integer totalForeigners;
    private Integer totalTours;
    private Integer totalIndividuals;
}

package org.hrd.finalprojectmuseum.model.entity.visitor;

import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class MuseumSchedule {
    private UUID scheduleId;
    private String day;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Boolean dayOff;
}

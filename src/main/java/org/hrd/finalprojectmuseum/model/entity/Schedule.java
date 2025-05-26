package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class Schedule {
    private UUID scheduleId;
    private String dayOfWeek;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Boolean dayOff;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

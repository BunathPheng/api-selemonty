package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.enums.DayOfWeek;

import java.time.LocalTime;

@Data
public class ScheduleRequest {

    @NotNull(message = "Day of week cannot be null")
    @Schema(description = "Day of the week", example = "MONDAY", allowableValues = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
    private DayOfWeek dayOfWeek;

    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "Opening time in HH:mm format", example = "09:00", type = "string", pattern = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    private LocalTime openingTime;

    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "Closing time in HH:mm format", example = "17:00", type = "string", pattern = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    private LocalTime closingTime;

    @NotNull(message = "Day off status cannot be null")
    @Schema(description = "Whether this day is a day off", example = "false")
    private Boolean dayOff;

    // Validation methods remain the same...
    @AssertTrue(message = "Opening time is required when it's not a day off")
    private boolean isOpeningTimeValid() {
        if (dayOff != null && !dayOff) {
            return openingTime != null;
        }
        return true;
    }

    @AssertTrue(message = "Closing time is required when it's not a day off")
    private boolean isClosingTimeValid() {
        if (dayOff != null && !dayOff) {
            return closingTime != null;
        }
        return true;
    }

    @AssertTrue(message = "Closing time must be after opening time")
    private boolean isTimeRangeValid() {
        if (openingTime != null && closingTime != null) {
            return closingTime.isAfter(openingTime);
        }
        return true;
    }
}
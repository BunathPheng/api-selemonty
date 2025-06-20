package org.hrd.finalprojectmuseum.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingStats {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime date;
    private Integer value;
}

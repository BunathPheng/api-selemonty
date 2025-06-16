package org.hrd.finalprojectmuseum.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StatItem {
    private Integer value;
    private Double percentageChange;
}

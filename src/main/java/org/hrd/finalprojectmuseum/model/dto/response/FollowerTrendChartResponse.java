package org.hrd.finalprojectmuseum.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Month;

@Data
@Builder
public class FollowerTrendChartResponse {
    private String month;
    private Integer followers;
    private String fullMonth;
}

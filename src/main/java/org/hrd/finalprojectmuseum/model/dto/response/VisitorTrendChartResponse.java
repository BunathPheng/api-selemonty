package org.hrd.finalprojectmuseum.model.dto.response;

import lombok.Data;

@Data
public class VisitorTrendChartResponse {
    private String month;
    private Integer visitors;
    private String fullMonth;
}
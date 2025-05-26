package org.hrd.finalprojectmuseum.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleGroupResponse {
    private String dayRange;
    private String timeRange;
    private boolean isDayOff;
}
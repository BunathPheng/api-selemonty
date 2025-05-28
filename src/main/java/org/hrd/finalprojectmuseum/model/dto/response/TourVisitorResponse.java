package org.hrd.finalprojectmuseum.model.dto.response;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.Guide;
import org.hrd.finalprojectmuseum.model.enums.TourStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class TourVisitorResponse {
    private UUID tourId;
    private String fullName;
    private BigDecimal tourPrice;
    private TourStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

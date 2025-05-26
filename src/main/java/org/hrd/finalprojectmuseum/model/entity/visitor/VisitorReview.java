package org.hrd.finalprojectmuseum.model.entity.visitor;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class VisitorReview {
    private UUID reviewId;
    private UUID museumId;
    private String fullName;
    private String comment;
    private BigDecimal rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isReviewed;
}

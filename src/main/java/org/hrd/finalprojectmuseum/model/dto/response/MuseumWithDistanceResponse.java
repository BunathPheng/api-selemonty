package org.hrd.finalprojectmuseum.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorReviewStatistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MuseumWithDistanceResponse {
    private UUID museumId;
    private String name;
    private String address;
    private String logoLink;
    private BigDecimal lat;
    private BigDecimal lng;
    private BigDecimal averageRating;
    private Integer totalReviews;
    private LocalTime openTime;
    private LocalTime closeTime;
    private BigDecimal distanceKm;
}
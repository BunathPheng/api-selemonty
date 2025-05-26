package org.hrd.finalprojectmuseum.model.entity.visitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisitorReviewStatistics {
    private BigDecimal averageRating;
    private Integer totalReviews;
    private Integer fiveStars;
    private Integer fourStars;
    private Integer threeStars;
    private Integer twoStars;
    private Integer oneStar;

    // Calculate percentages
    public Integer getFiveStarPercentage() {
        return totalReviews > 0 ? Math.round((fiveStars * 100.0f) / totalReviews) : 0;
    }

    public Integer getFourStarPercentage() {
        return totalReviews > 0 ? Math.round((fourStars * 100.0f) / totalReviews) : 0;
    }

    public Integer getThreeStarPercentage() {
        return totalReviews > 0 ? Math.round((threeStars * 100.0f) / totalReviews) : 0;
    }

    public Integer getTwoStarPercentage() {
        return totalReviews > 0 ? Math.round((twoStars * 100.0f) / totalReviews) : 0;
    }

    public Integer getOneStarPercentage() {
        return totalReviews > 0 ? Math.round((oneStar * 100.0f) / totalReviews) : 0;
    }
}

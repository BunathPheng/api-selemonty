package org.hrd.finalprojectmuseum.model.entity.visitor;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class VisitorFavorite {
    private UUID favoriteId;
    private UUID museumId;
    private UUID visitorId;
    private Boolean isFavorite;
    private LocalDateTime createdAt;
}

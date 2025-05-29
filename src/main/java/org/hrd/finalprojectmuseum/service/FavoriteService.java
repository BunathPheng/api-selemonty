package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorFavorite;
import org.hrd.finalprojectmuseum.model.enums.FavoriteType;

import java.util.UUID;

public interface FavoriteService {
    void addVisitorFavorite(UUID museumId, UUID visitorId, FavoriteType favoriteType);
    VisitorFavorite getVisitorFavorite(UUID museumId, UUID visitorId);
}

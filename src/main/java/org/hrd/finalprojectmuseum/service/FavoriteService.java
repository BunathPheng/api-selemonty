package org.hrd.finalprojectmuseum.service;

import jakarta.validation.constraints.Min;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.FavoriteMuseum;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorFavorite;
import org.hrd.finalprojectmuseum.model.enums.FavoriteType;

import java.util.List;
import java.util.UUID;

public interface FavoriteService {
    void addVisitorFavorite(UUID museumId, UUID visitorId, FavoriteType favoriteType);
    VisitorFavorite getVisitorFavorite(UUID museumId, UUID visitorId);
    ListResponse<FavoriteMuseum> getAllFavoriteMuseums(UUID visitorId, Integer page, Integer size);
}

package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumWithDistanceResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface MuseumService {
    MuseumOwner setFullData(MuseumOwner museumOwner);

    ListResponse<MuseumOwner> getAllRequestMuseum(UUID museumCategoryId, Integer page, Integer size);

    void approveMuseum(UUID museumId);

    ListResponse<MuseumOwner> getAllMuseum(String search, UUID categoryId, Integer page, Integer size);

    ListResponse<MuseumOwner> getAllApprovedMuseum(UUID museumCategoryId, Integer page, Integer size);

    List<MuseumWithDistanceResponse> getAllMuseumByLocation(BigDecimal lat, BigDecimal lng, Integer distance);
}

package org.hrd.finalprojectmuseum.service;

import jakarta.validation.constraints.NotNull;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.MuseumWithDistanceResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.enums.MuseumStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface MuseumService {
    MuseumOwner setFullData(MuseumOwner museumOwner);

    void approveMuseum(UUID museumId);

    ListResponse<MuseumOwner> getAllMuseum(String search, UUID categoryId, Integer page, Integer size, MuseumStatus museumStatus);

    List<MuseumWithDistanceResponse> getAllMuseumByLocation(BigDecimal lat, BigDecimal lng, Integer distance);

    MuseumOwner getAllMuseumByMuseumId(@NotNull UUID museumId);
}

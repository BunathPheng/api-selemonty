package org.hrd.finalprojectmuseum.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.GuideRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Guide;
import org.hrd.finalprojectmuseum.model.enums.GuideStatusType;

import java.util.UUID;

public interface GuideService {
    ListResponse<Guide> getAllTourGuideByMuseumId(UUID museumId, String search, Integer page, Integer size, @NotNull GuideStatusType statusType);

    Guide addNewGuideByMuseumId(UUID museumId, @Valid GuideRequest guideRequest);

    Guide updateGuideByGuideId(UUID museumId, @NotNull UUID guideId, @Valid GuideRequest guideRequest);
}

package org.hrd.finalprojectmuseum.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hrd.finalprojectmuseum.model.dto.request.AcceptTourRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.TourVisitorResponse;
import org.hrd.finalprojectmuseum.model.entity.Tour;
import org.hrd.finalprojectmuseum.model.enums.TourStatus;

import java.util.UUID;

public interface TourService {
    ListResponse<Tour> getAllTourByMuseumId(UUID id, String search, Integer page, Integer size, TourStatus statusType);

    Tour getTourByTourId(UUID museumId, @NotNull(message = "TourID can't be null") UUID tourId);

    Tour acceptTourByTourId(UUID tourId, AcceptTourRequest acceptTourRequest);

    ListResponse<Tour> getAllTourByVisitorId(UUID visitorId, String search, Integer page, Integer size, TourStatus statusType);

    Tour getTourByTourIdWithVisitorId(UUID visitorId, @NotNull(message = "TourID can't be null") UUID tourId);

    void updateTourStatus(@NotNull(message = "TourID can't be null") UUID tourId);
}

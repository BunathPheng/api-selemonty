package org.hrd.finalprojectmuseum.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.TourVisitorResponse;
import org.hrd.finalprojectmuseum.model.entity.Tour;
import org.hrd.finalprojectmuseum.model.enums.TourStatus;

import java.util.UUID;

public interface TourService {
    ListResponse<TourVisitorResponse> getAllTourByMuseumId(UUID museumId, String search, @Min(value = 1, message = "must be greater than 0") Integer page, @Min(value = 1, message = "must be greater than 0") Integer size, @NotNull(message = "Status Type is required") TourStatus statusType);

    Tour getTourByTourId(UUID museumId, @NotNull(message = "TourID can't be null") UUID tourId);
}

package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.TourVisitorResponse;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.Tour;
import org.hrd.finalprojectmuseum.model.enums.TourStatus;
import org.hrd.finalprojectmuseum.repository.TourRepository;
import org.hrd.finalprojectmuseum.service.TourService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {
    private final TourRepository tourRepository;

    @Override
    public ListResponse<TourVisitorResponse> getAllTourByMuseumId(UUID museumId, String search, Integer page, Integer size, TourStatus statusType) {
        search = search == null ? "" : search;
        List<TourVisitorResponse> tours;
        Integer allItems;
        if (statusType == TourStatus.All){
            tours = tourRepository.findAllTourByMuseumId(museumId, search, page, size);
            allItems = tourRepository.countAllTour(museumId, search);
        }else{
            tours = tourRepository.findAllTourByMuseumIdWithStatus(museumId, search, page, size, statusType.toString());
            allItems = tourRepository.countAllTourWithStatus(museumId, search, statusType.toString());
        }
        Pagination pagination = new Pagination();

        return ListResponse.<TourVisitorResponse>builder()
                .pagination(pagination.calculatePagination(allItems, page, size))
                .items(tours)
                .build();
    }

    @Override
    public Tour getTourByTourId(UUID museumId, UUID tourId) {
        Tour tour = tourRepository.getTourByTourId(museumId, tourId);
        if(tour == null){
            throw new AppNotFoundException("Tour with id " + tourId + " not found");
        }
        return tour;
    }
}

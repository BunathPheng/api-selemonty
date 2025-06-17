package org.hrd.finalprojectmuseum.service.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.GuideRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Guide;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.enums.GuideStatusType;
import org.hrd.finalprojectmuseum.repository.GuideRepository;
import org.hrd.finalprojectmuseum.service.GuideService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {

    private final GuideRepository guideRepository;

    @Override
    public ListResponse<Guide> getAllTourGuideByMuseumId(UUID museumId, String search, Integer page, Integer size, @NotNull GuideStatusType statusType) {
        search = (search == null) ? "" : search;
        Boolean isAvailable = statusType == GuideStatusType.AVAILABLE;
        List<Guide> guides;
        Integer allItems;
        if (statusType == GuideStatusType.ALL){
            guides = guideRepository.findAllGuideByMuseumId(museumId, search, page, size);
            allItems = guideRepository.countAllGuide(museumId, search);
        }else{
            guides = guideRepository.findAllGuideByMuseumIdWithType(museumId, search, page, size, isAvailable);
            allItems = guideRepository.countAllGuideWithType(museumId, search, isAvailable);
        }

        Pagination pagination = new Pagination();
        return ListResponse.<Guide>builder()
                .items(guides)
                .pagination(pagination.calculatePagination(allItems, page, size))
                .build();
    }

    @Override
    public Guide addNewGuideByMuseumId(UUID museumId, GuideRequest guideRequest) {
        return guideRepository.insertGuideByMuseumId(museumId, guideRequest);
    }

    @Override
    public Guide updateGuideByGuideId(UUID museumId, UUID guideId, GuideRequest guideRequest) {
        Guide guide = guideRepository.findGuideByGuideId(guideId);
        if (guide == null) {
            throw new AppNotFoundException("Guide with id " + guideId + " not found");
        }
        return guideRepository.modifyGuideByGuideId(museumId, guideId, guideRequest, LocalDateTime.now());
    }

    @Override
    public List<Guide> getGuidesByTourId(UUID tourId) {
        return guideRepository.getGuidesByTourId(tourId);
    }

}

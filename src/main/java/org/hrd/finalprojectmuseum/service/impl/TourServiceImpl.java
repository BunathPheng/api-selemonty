package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.AcceptTourRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.dto.response.TourVisitorResponse;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.TicketInfo;
import org.hrd.finalprojectmuseum.model.entity.Tour;
import org.hrd.finalprojectmuseum.model.enums.TourStatus;
import org.hrd.finalprojectmuseum.repository.BookingRepository;
import org.hrd.finalprojectmuseum.repository.TicketInfoRepository;
import org.hrd.finalprojectmuseum.repository.TourRepository;
import org.hrd.finalprojectmuseum.service.AppUserService;
import org.hrd.finalprojectmuseum.service.ProfileService;
import org.hrd.finalprojectmuseum.service.TicketInfoService;
import org.hrd.finalprojectmuseum.service.TourService;
import org.hrd.finalprojectmuseum.utils.UniqueTextCodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {
    private final TourRepository tourRepository;
    private final UniqueTextCodeGenerator uniqueTextCodeGenerator;
    private final BookingRepository bookingRepository;
    private final TicketInfoRepository ticketInfoRepository;
    private final ProfileService profileService;

    @Override
    public ListResponse<Tour> getAllTourByMuseumId(UUID id, String search, Integer page, Integer size, TourStatus statusType) {
        search = search == null ? "" : search;
        List<Tour> tours;
        Integer allItems;
        if (statusType == TourStatus.ALL){
            tours = tourRepository.findAllTourByMuseumId(id, search, page, size);
            allItems = tourRepository.countAllTour(id, search);
        }else{
            tours = tourRepository.findAllTourByMuseumIdWithStatus(id, search, page, size, statusType.toString());
            allItems = tourRepository.countAllTourWithStatus(id, search, statusType.toString());
        }
        Pagination pagination = new Pagination();

        return ListResponse.<Tour>builder()
                .pagination(pagination.calculatePagination(allItems, page, size))
                .items(tours)
                .build();
    }

    @Override
    public ListResponse<Tour> getAllTourByVisitorId(UUID id, String search, Integer page, Integer size, TourStatus statusType) {
        search = search == null ? "" : search;
        List<Tour> tours;
        Integer allItems;
        if (statusType == TourStatus.ALL){
            tours = tourRepository.findAllTourByVisitorId(id, search, page, size);
            allItems = tourRepository.countAllTourByVisitorId(id, search);
        }else{
            tours = tourRepository.findAllTourByVisitorIdWithStatus(id, search, page, size, statusType.toString());
            allItems = tourRepository.countAllTourWithStatusByVisitorId(id, search, statusType.toString());
        }
        Pagination pagination = new Pagination();

        return ListResponse.<Tour>builder()
                .pagination(pagination.calculatePagination(allItems, page, size))
                .items(tours)
                .build();
    }

    @Override
    public Tour getTourByTourIdWithVisitorId(UUID visitorId, UUID tourId) {
        Tour tour = tourRepository.getTourByTourIdWithVisitorId(visitorId, tourId);
        if(tour == null){
            throw new AppNotFoundException("Tour with id " + tourId + " not found");
        }

        return tour;
    }

    @Transactional
    @Override
    public void updateTourStatus(UUID tourId) {
        Tour tour = tourRepository.findTourByTourId(tourId);
        if(tour == null){
            throw new AppNotFoundException("Tour with id " + tourId + " not found");
        }
        if (tour.getStatus() == TourStatus.REQUEST){
            throw new AppBadRequestException("Tour is requesting. Wait for museum accept first");
        } else if (tour.getStatus() == TourStatus.PAID) {
            throw new AppBadRequestException("Tour is already paid");
        }
        UUID bookingId = tourRepository.modifyTourStatus(tourId, TourStatus.PAID.toString(), LocalDateTime.now());
        String code = uniqueTextCodeGenerator.generateUniqueTextCode();
        bookingRepository.setTicketCode(bookingId, code);

        UUID museumId = profileService.getMuseumIdByUserId();
        Integer requestSlot = tourRepository.getRequestSlot(tourId);
        TicketInfo ticketInfo = ticketInfoRepository.findTicketInfoByMuseumId(museumId);
        System.out.println("ticketInfo: " + museumId);
        Integer updatedAmount = ticketInfo.getTotalSlot() - requestSlot;
        ticketInfoRepository.updateSlotAmount(museumId, updatedAmount);
    }

    @Override
    public Tour getTourByTourId(UUID museumId, UUID tourId) {
        Tour tour = tourRepository.getTourByTourId(museumId, tourId);
        if(tour == null){
            throw new AppNotFoundException("Tour with id " + tourId + " not found");
        }

        return tour;
    }

    @Transactional
    @Override
    public Tour acceptTourByTourId(UUID tourId, AcceptTourRequest acceptTourRequest) {
        UUID museumId = profileService.getMuseumIdByUserId();
        Tour tour = tourRepository.findTourByTourId(tourId);
        if(tour == null){
            throw new AppNotFoundException("Tour with id " + tourId + " not found");
        }
        if (tour.getStatus() != TourStatus.REQUEST){
            throw new AppBadRequestException("Tour already accepted. Tour status is " + tour.getStatus());
        }
        tourRepository.setTourPrice(tourId, acceptTourRequest.getTourPrice());
        for (UUID guideId : acceptTourRequest.getGuideId()){
            tourRepository.setTourGuys(tourId, guideId);
        }
        return tourRepository.findTourByTourId(tourId);
    }
}

package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.FollowerStat;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.FavoriteMuseum;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorFavorite;
import org.hrd.finalprojectmuseum.model.enums.FavoriteType;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.repository.*;
import org.hrd.finalprojectmuseum.service.AppUserService;
import org.hrd.finalprojectmuseum.service.FavoriteService;
import org.hrd.finalprojectmuseum.service.MuseumService;
import org.hrd.finalprojectmuseum.utils.Calculation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
    private final AppUserService appUserService;
    private final Calculation calculation = new Calculation();
    private final ProfileRepository profileRepository;
    private final BookingRepository bookingRepository;
    private final MuseumRepository museumRepository;

    @Override
    public void addVisitorFavorite(UUID museumId, UUID visitorId, FavoriteType favoriteType) {
        if (!reviewRepository.retrieveMuseumId(museumId) || !favoriteRepository.isApproveMuseum(museumId)) {
            throw new AppNotFoundException("Museum not found");
        }

        Boolean currentStatus = favoriteRepository.getCurrentFavoriteStatus(museumId, visitorId);
        boolean newStatus = (favoriteType == FavoriteType.FAVORITE);

        if (currentStatus == null) {
            favoriteRepository.addVisitorFavorite(museumId, visitorId, newStatus);
        } else {
            if (currentStatus && newStatus) {
                throw new AppBadRequestException("The museum already added to favorite");
            }else if (!currentStatus && !newStatus) {
                throw new AppBadRequestException("The museum already added to unfavorite");
            }
            favoriteRepository.updateVisitorFavorite(museumId, visitorId, newStatus);
        }
    }

    @Override
    public VisitorFavorite getVisitorFavorite(UUID museumId, UUID visitorId) {
        if (!reviewRepository.retrieveMuseumId(museumId) || !favoriteRepository.isApproveMuseum(museumId)) {
            throw new AppNotFoundException("Museum not found");
        }else if (favoriteRepository.isMuseumUnFavoriteByVisitor(museumId, visitorId)) {
            throw new AppBadRequestException("Visitor favorite museum already exists");
        }else if (!favoriteRepository.isMuseumFavoriteByVisitor(museumId, visitorId)) {
            throw new AppBadRequestException("Visitor haven't been add museum to favorite yet");
        }
        return favoriteRepository.getFavoriteByIds(museumId, visitorId);
    }

    @Override
    public ListResponse<FavoriteMuseum> getAllFavoriteMuseums(UUID visitorId, Integer page, Integer size) {
//        if(favoriteRepository.retrieveFavoriteMuseums(visitorId, page, size).isEmpty()) {
//            throw new AppNotFoundException("Museum not found");
//        }
        List<FavoriteMuseum> favoriteMuseums = favoriteRepository.retrieveFavoriteMuseums(visitorId, page, size);
        Integer total = favoriteRepository.countFavoriteMuseum(visitorId);
        Pagination pagination = new Pagination();
        return ListResponse.<FavoriteMuseum>builder()
                .items(favoriteMuseums)
                .pagination(pagination.calculatePagination(total, page, size))
                .build();
    }

    @Override
    public FollowerStat getFollowerStat(UUID museumId) {
        UUID userId = appUserService.getUserId();
        MuseumOwner museumOwner = museumRepository.findMuseumByMuseumId(museumId);
        AppUserRegister appUserRegister = appUserService.findUserByUserId(userId);

        if (appUserRegister.getRole() == Role.ROLE_MUSEUM_OWNER){
            UUID idMuseumLogin = profileRepository.findMuseumOwnerByUserId(userId).getMuseumId();
            if (!idMuseumLogin.equals(museumId)) {
                throw new AppBadRequestException("Museum " + museumId + " not found, Resource restriction");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayEnd = now.toLocalDate().atTime(23, 59, 59);

        LocalDate currentMonthStart = now.toLocalDate().withDayOfMonth(1);
        LocalDateTime currentMonthStartTime = currentMonthStart.atStartOfDay();

        LocalDate lastMonthStart = currentMonthStart.minusMonths(1);
        LocalDateTime lastMonthStartTime = lastMonthStart.atStartOfDay();
        LocalDate lastMonthEnd = currentMonthStart.minusDays(1);
        LocalDateTime lastMonthEndTime = lastMonthEnd.atTime(23, 59, 59);

        Integer totalFollower = favoriteRepository.countFollowerByMuseumIdAndEndDate(museumOwner.getMuseumId(), todayEnd);
        Integer totalLastMonthFollower = favoriteRepository.countFollowerByMuseumIdAndEndDate(museumOwner.getMuseumId(), lastMonthEndTime);

        Integer newFollower = favoriteRepository.countFollowerByMuseumIdAndDateRange(museumOwner.getMuseumId(), currentMonthStartTime, todayEnd);
        Integer lastMonthNewFollower = favoriteRepository.countFollowerByMuseumIdAndDateRange(museumOwner.getMuseumId(), lastMonthStartTime, lastMonthEndTime);

        Integer newBooking = bookingRepository.countNewBookingByMuseumId(museumOwner.getMuseumId(), currentMonthStartTime, todayEnd);
        Integer lastMonthNewBooking = bookingRepository.countNewBookingByMuseumId(museumOwner.getMuseumId(), lastMonthStartTime, lastMonthEndTime);
        return FollowerStat.builder()
                .totalFollowers(calculation.addStatItem(totalFollower, totalLastMonthFollower))
                .newFollowers(calculation.addStatItem(newFollower, lastMonthNewFollower))
                .newBooking(calculation.addStatItem(newBooking, lastMonthNewBooking))
                .build();
    }
}

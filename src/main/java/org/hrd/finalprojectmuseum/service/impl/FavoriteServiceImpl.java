package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.FavoriteMuseum;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorFavorite;
import org.hrd.finalprojectmuseum.model.enums.FavoriteType;
import org.hrd.finalprojectmuseum.repository.FavoriteRepository;
import org.hrd.finalprojectmuseum.repository.ReviewRepository;
import org.hrd.finalprojectmuseum.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public void addVisitorFavorite(UUID museumId, UUID visitorId, FavoriteType favoriteType) {
        System.out.println("hello world");
        if (!reviewRepository.retrieveMuseumId(museumId) || !favoriteRepository.isApproveMuseum(museumId)) {
            throw new AppNotFoundException("Museum not found");
        }
        if (favoriteType == FavoriteType.FAVORITE) {
            if (favoriteRepository.isMuseumFavoriteByVisitor(museumId, visitorId)) {
                throw new AppBadRequestException("Museum already added to visitor favorite");
            }else if (favoriteRepository.isMuseumUnFavoriteByVisitor(museumId, visitorId)) {
                favoriteRepository.updateVisitorFavorite(museumId, visitorId, true);
            }else{
                favoriteRepository.addVisitorFavorite(museumId, visitorId, true);
            }
        } else if (favoriteType == FavoriteType.UNFAVORITE) {
            if (!favoriteRepository.isMuseumFavoriteByVisitor(museumId, visitorId)) {
                throw new AppBadRequestException("Museum is not in your favorites");
            }
            favoriteRepository.updateVisitorFavorite(museumId, visitorId, false);
        } else {
            throw new IllegalArgumentException("Invalid favorite type: " + favoriteType);
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
        List<FavoriteMuseum> favoriteMuseums = favoriteRepository.retrieveFavoriteMuseums(visitorId, page, size);
        Integer total = favoriteRepository.countFavoriteMuseum(visitorId);
        Pagination pagination = new Pagination();
        return ListResponse.<FavoriteMuseum>builder()
                .items(favoriteMuseums)
                .pagination(pagination.calculatePagination(total, page, size))
                .build();
    }
}

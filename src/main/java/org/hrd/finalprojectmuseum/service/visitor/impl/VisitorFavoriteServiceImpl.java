package org.hrd.finalprojectmuseum.service.visitor.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.entity.visitor.VisitorFavorite;
import org.hrd.finalprojectmuseum.model.enums.FavoriteType;
import org.hrd.finalprojectmuseum.repository.visitor.VisitorFavoriteRepository;
import org.hrd.finalprojectmuseum.repository.visitor.VisitorReviewRepository;
import org.hrd.finalprojectmuseum.service.visitor.VisitorFavoriteService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitorFavoriteServiceImpl implements VisitorFavoriteService {
    private final VisitorFavoriteRepository visitorFavoriteRepository;
    private final VisitorReviewRepository visitorReviewRepository;

    @Override
    public void addVisitorFavorite(UUID museumId, UUID visitorId, FavoriteType favoriteType) {
        if (!visitorReviewRepository.retrieveMuseumId(museumId)) {
            throw new AppNotFoundException("Museum not found");
        }
        if (favoriteType == FavoriteType.FAVORITE) {
            if (visitorFavoriteRepository.isMuseumFavoriteByVisitor(museumId, visitorId)) {
                throw new AppBadRequestException("Museum already added to visitor favorite");
            }else if (visitorFavoriteRepository.isMuseumUnFavoriteByVisitor(museumId, visitorId)) {
                visitorFavoriteRepository.updateVisitorFavorite(museumId, visitorId, true);
            }else{
                visitorFavoriteRepository.addVisitorFavorite(museumId, visitorId, true);
            }
        } else if (favoriteType == FavoriteType.UNFAVORITE) {
            if (!visitorFavoriteRepository.isMuseumFavoriteByVisitor(museumId, visitorId)) {
                throw new AppBadRequestException("Museum is not in your favorites");
            }
            visitorFavoriteRepository.updateVisitorFavorite(museumId, visitorId, false);
        } else {
            throw new IllegalArgumentException("Invalid favorite type: " + favoriteType);
        }
    }

    @Override
    public VisitorFavorite getVisitorFavorite(UUID museumId, UUID visitorId) {
        if (!visitorReviewRepository.retrieveMuseumId(museumId)) {
            throw new AppNotFoundException("Museum not found");
        }else if (visitorFavoriteRepository.isMuseumUnFavoriteByVisitor(museumId, visitorId)) {
            throw new AppBadRequestException("Visitor favorite museum already exists");
        }
        return visitorFavoriteRepository.getFavoriteByIds(museumId, visitorId);
    }
}

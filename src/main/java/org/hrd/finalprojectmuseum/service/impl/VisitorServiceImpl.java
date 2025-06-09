package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.repository.ProfileRepository;
import org.hrd.finalprojectmuseum.repository.VisitorRepository;
import org.hrd.finalprojectmuseum.service.VisitorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private final VisitorRepository visitorRepository;
    private final ProfileRepository profileRepository;

    @Override
    public ListResponse<Visitor> getVisitorByUserId(UUID userId, String search, Integer page, Integer size) {
        MuseumOwner museumOwner = profileRepository.findMuseumOwnerByUserId(userId);
        if (museumOwner == null){
            throw new AppNotFoundException("User with id " + userId + " not found");
        }
        List<Visitor> visitors = visitorRepository.findVisitorByMuseumId(museumOwner.getMuseumId(), search, page, size);
        Integer allItem = visitorRepository.countAllVisitorByMuseumId(museumOwner.getMuseumId(), search);
        Pagination pagination = new Pagination();
        return ListResponse.<Visitor>builder()
                .items(visitors)
                .pagination(pagination.calculatePagination(allItem, page, size))
                .build();
    }

    @Override
    public ListResponse<Visitor> getAllVisitor(String search, Integer page, Integer size) {
        search = search == null ? "" : search;
        List<Visitor> visitors = visitorRepository.findAllVisitor(search, page, size);
        Integer allItem = visitorRepository.countAllVisitor(search);
        Pagination pagination = new Pagination();
        return ListResponse.<Visitor>builder()
                .items(visitors)
                .pagination(pagination.calculatePagination(allItem, page, size))
                .build();
    }

    @Override
    public Visitor getVisitorById(UUID visitorId) {
        Visitor visitor = visitorRepository.findVisitorById(visitorId);
        if (visitor == null){
            throw new AppNotFoundException("Visitor with id " + visitorId + " not found");
        }
        return visitor;
    }
}

package org.hrd.finalprojectmuseum.service.impl;


import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppBadRequestException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumShortInfo;
import org.hrd.finalprojectmuseum.repository.MuseumManagementForAdminRepository;
import org.hrd.finalprojectmuseum.repository.MuseumOwnerRepository;
import org.hrd.finalprojectmuseum.repository.TicketInfoRepository;
import org.hrd.finalprojectmuseum.service.MuseumManagementForAdminService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MuseumManagementForAdminServiceImpl implements MuseumManagementForAdminService {

    private final MuseumManagementForAdminRepository museumManagementForAdminRepository;
    private final MuseumOwnerRepository museumOwnerRepository;

    @Override
    public ListResponse<MuseumShortInfo> getAllRequestMuseum(UUID museumCategoryId, Integer page, Integer size) {
        List<MuseumShortInfo> museums;
        if (museumCategoryId == null){
            museums = museumManagementForAdminRepository.getAllRequestMuseums(page, size);
        }else {
            museums = museumManagementForAdminRepository.getAllRequestMuseumsByCategoryId(museumCategoryId, page, size);
        }
        Integer totalItems = museumManagementForAdminRepository.countAllRequestMuseums();

        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);

        ListResponse<MuseumShortInfo> listMuseumResponse = new ListResponse<>();
        listMuseumResponse.setItems(museums);
        listMuseumResponse.setPagination(pagination);
        return listMuseumResponse;
    }


    @Override
    public void approveMuseum(UUID museumId) {
        MuseumOwner museum = museumOwnerRepository.findMuseumOwnerByMuseumId(museumId);
        if(museum == null) {
            throw new AppNotFoundException("Museum not found. Please check museum id and try again.");
        }
        if (museum.getIsApproved()) {
            throw new AppBadRequestException("Museum is already approved.");
        }
        museumManagementForAdminRepository.udpateIsApprovedStatus(museumId);

    }

    @Override
    public ListResponse<MuseumShortInfo> getAllMuseum(UUID museumCategoryId, Integer page, Integer size) {
        List<MuseumShortInfo> museums;
        if (museumCategoryId == null){
            museums = museumManagementForAdminRepository.getAllMuseums(page, size);
        }else {
            museums = museumManagementForAdminRepository.getAllMuseumsByCategoryId(museumCategoryId, page, size);
        }

        Integer totalItems = museumManagementForAdminRepository.countAllMuseums();

        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);

        ListResponse<MuseumShortInfo> listMuseumResponse = new ListResponse<>();
        listMuseumResponse.setItems(museums);
        listMuseumResponse.setPagination(pagination);
        return listMuseumResponse;
    }

    @Override
    public ListResponse<MuseumShortInfo> getAllApprovedMuseum(UUID museumCategoryId, Integer page, Integer size) {
        List<MuseumShortInfo> museums;
        if (museumCategoryId == null){
            museums = museumManagementForAdminRepository.getAllApprovedMuseums(page, size);
        }else {
            museums = museumManagementForAdminRepository.getAllApprovedMuseumsByCategoryId(museumCategoryId, page, size);
        }

        Integer totalItems = museumManagementForAdminRepository.countAllApprovedMuseums();

        Pagination pagination = new Pagination();
        pagination = pagination.calculatePagination(totalItems, page, size);

        ListResponse<MuseumShortInfo> listMuseumResponse = new ListResponse<MuseumShortInfo>();
        listMuseumResponse.setItems(museums);
        listMuseumResponse.setPagination(pagination);
        return listMuseumResponse;
    }
}

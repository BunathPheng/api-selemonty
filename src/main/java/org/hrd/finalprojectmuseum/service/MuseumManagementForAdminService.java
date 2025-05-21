package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumShortInfo;

import java.util.UUID;

public interface MuseumManagementForAdminService {
    ListResponse<MuseumShortInfo> getAllRequestMuseum(UUID museumCategoryId, Integer page, Integer size);

    void approveMuseum(UUID museumId);

    ListResponse<MuseumShortInfo> getAllMuseum(UUID categoryId, Integer page, Integer size);

    ListResponse<MuseumShortInfo> getAllApprovedMuseum(UUID museumCategoryId, Integer page, Integer size);
}

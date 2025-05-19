package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.dto.response.ListMuseumResponse;
import java.util.UUID;

public interface MuseumManagementForAdminService {
    ListMuseumResponse getAllRequestMuseum(UUID museumCategoryId, Integer page, Integer size);

    void approveMuseum(UUID museumId);

    ListMuseumResponse getAllMuseum(UUID categoryId, Integer page, Integer size);

    ListMuseumResponse getAllApprovedMuseum(UUID museumCategoryId, Integer page, Integer size);
}

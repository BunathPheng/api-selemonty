package org.hrd.finalprojectmuseum.service;

import jakarta.validation.constraints.NotNull;
import org.hrd.finalprojectmuseum.model.dto.request.admin.AdminRequest;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;

import java.util.List;
import java.util.UUID;

public interface AdminService {

    Admin getAdminByUserId(UUID userId);

    Admin updateAdminByUserId(UUID userId, AdminRequest adminRequest);


    List<MuseumOwner> getAllRequestMuseum();

    void approveMuseum(@NotNull UUID museumId);

    List<MuseumOwner> getAllMuseum();

    List<MuseumOwner> getAllApprovedMuseum();
}

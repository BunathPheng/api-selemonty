package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.dto.request.admin.AdminRequest;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;

import java.util.UUID;

public interface AdminService {

    Admin getAdminByUserId(UUID userId);

    Admin updateAdminByUserId(UUID userId, AdminRequest adminRequest);


}

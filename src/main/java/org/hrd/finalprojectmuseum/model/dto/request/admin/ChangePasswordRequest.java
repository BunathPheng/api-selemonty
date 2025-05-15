package org.hrd.finalprojectmuseum.model.dto.request.admin;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}

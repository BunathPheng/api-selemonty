package org.hrd.finalprojectmuseum.model.dto.request.auth;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}

package org.hrd.finalprojectmuseum.model.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IdTokenRequest {
    @NotBlank(message = "IdToken is required")
    private String idToken;
}

package org.hrd.finalprojectmuseum.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentAccountRequest {
    @NotEmpty(message = "client Id is required")
    @Size(max = 255, min = 8, message = "Client Id must be between 8 to 255 length")
    private String clientId;

    @NotEmpty(message = "Client Secret is required")
    @Size(max = 255, min = 8, message = "Client Secret must be between 8 to 255 length")
    private String clientSecret;

    @NotEmpty(message = "Client Secret is required")
    @Size(max = 255, min = 4, message = "Account Name must be between 4 to 255 length")
    private String accountName;

    @NotEmpty(message = "Parent account no is required")
    @Size(max = 255, min = 4, message = "Parent account no must be between 4 to 255 length")
    private String parentAccountNo;
}

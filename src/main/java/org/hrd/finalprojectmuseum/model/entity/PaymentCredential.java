package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;

@Data
public class PaymentCredential {
    private String clientId;
    private String clientSecret;
    private String accountName;
    private String parentAccountNumber;
}

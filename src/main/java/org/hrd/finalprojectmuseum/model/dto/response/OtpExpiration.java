package org.hrd.finalprojectmuseum.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpExpiration {
    long expiration;
}

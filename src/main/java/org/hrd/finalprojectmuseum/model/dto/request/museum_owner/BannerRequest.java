package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BannerRequest {
    @Pattern(
            regexp = "^(https?:\\/\\/.*\\.(?:png|jpg|jpeg|gif))$",
            message = "Must be a valid image URL (jpg, jpeg, png, gif)"
    )
    @NotBlank(message = "Banner link is required")
    private String bannerLink;
}

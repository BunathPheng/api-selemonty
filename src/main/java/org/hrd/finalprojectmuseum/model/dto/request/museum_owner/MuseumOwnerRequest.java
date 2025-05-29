package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hrd.finalprojectmuseum.utils.validateJsonB.ValidJson;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MuseumOwnerRequest {

    private UUID museumCategoryId;

    @Pattern(
            regexp = "^[a-zA-Z][a-zA-Z0-9\\s]{1,254}$",
            message = "Name must start with a letter and be 2–255 characters"
    )
    @NotBlank(message = "Name is required")
    private String name;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Must be a valid phone number")
    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @Digits(integer = 3, fraction = 6, message = "Must be a valid coordinate")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal lat;

    @Digits(integer = 3, fraction = 6, message = "Must be a valid coordinate")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal lng;

    @Pattern(
            regexp = "^(https?:\\/\\/.*\\.(?:png|jpg|jpeg|gif))$",
            message = "Must be a valid image URL (jpg, jpeg, png, gif)"
    )
    @NotBlank(message = "Logo link is required")
    private String logoLink;

    @Pattern(
            regexp = "^(https?:\\/\\/.*\\.(?:png|jpg|jpeg|gif))$",
            message = "Must be a valid image URL (jpg, jpeg, png, gif)"
    )
    @NotBlank(message = "Banner link is required")
    private String bannerLink;

    @ValidJson
    @NotNull(message = "Landscape link is required")
    private JSONObject landscapeLink;

    @Pattern(
            regexp = "^[a-zA-Z0-9\\s]{2,2000}$",
            message = "Description must be 2-2000 characters"
    )
    @NotBlank(message = "Description is required")
    private String description;
}

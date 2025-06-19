package org.hrd.finalprojectmuseum.model.dto.request.museum_owner;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hrd.finalprojectmuseum.utils.validateJsonB.ValidJson;

@Data
public class LandscapeRequest {
    @ValidJson(message = "Key name can not be duplicate and value must be correct link format")
    @NotNull(message = "Landscape link is required")
    private JSONObject landscapeLink;
}

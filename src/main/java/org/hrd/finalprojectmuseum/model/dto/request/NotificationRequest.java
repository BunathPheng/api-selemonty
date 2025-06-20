package org.hrd.finalprojectmuseum.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NotificationRequest {
    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("included_segments")
    private List<String> includedSegments;

    @JsonProperty("include_player_ids")
    private List<String> includePlayerIds;

    private Map<String, String> contents;
    private Map<String, String> headings;
    private Map<String, Object> data;
    private String url;
}
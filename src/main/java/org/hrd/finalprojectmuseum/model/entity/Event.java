package org.hrd.finalprojectmuseum.model.entity;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;


import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Event {
    private UUID eventId;
    private MuseumOwner museum;
    private String title;
    private String subTitle;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private JSONObject imageLinks;
    private String curator;
    private String accessibilityNote;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;

    public void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
        if ((now.isEqual(this.startDate) || now.isAfter(this.startDate)) && now.isBefore(this.endDate)) {
            this.status = "Ongoing";
        } else if (now.isBefore(this.startDate)) {
            this.status = "Coming soon";
        } else if (now.isAfter(this.endDate)) {
            this.status = "Finished";
        }
    }
}


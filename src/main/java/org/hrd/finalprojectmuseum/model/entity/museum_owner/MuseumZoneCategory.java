package org.hrd.finalprojectmuseum.model.entity.museum_owner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MuseumZoneCategory {
    private UUID museumZoneCategoryId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

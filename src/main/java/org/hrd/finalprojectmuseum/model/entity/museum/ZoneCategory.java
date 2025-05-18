package org.hrd.finalprojectmuseum.model.entity.museum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZoneCategory {
    private UUID id;
    private String name;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}

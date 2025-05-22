package org.hrd.finalprojectmuseum.model.entity.museum_owner;

import lombok.Data;

import java.util.UUID;

@Data
public class MuseumCategory {
    private UUID museumCategoryId;
    private String name;
}

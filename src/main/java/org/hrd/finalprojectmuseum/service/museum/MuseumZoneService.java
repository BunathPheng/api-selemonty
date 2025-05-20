package org.hrd.finalprojectmuseum.service.museum;

import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;

import java.util.List;

public interface MuseumZoneService {
    List<MuseumZoneCategory> getAllZonesCategories();
    MuseumZone createMuseumZone(MuseumZoneRequest museumZoneRequest);
}

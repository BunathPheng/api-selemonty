package org.hrd.finalprojectmuseum.service.museum.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;
import org.hrd.finalprojectmuseum.repository.museum.MuseumZoneRepository;
import org.hrd.finalprojectmuseum.service.museum.MuseumZoneService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MuseumZoneServiceImpl implements MuseumZoneService {

    private final MuseumZoneRepository museumZoneRepository;

    @Override
    public List<MuseumZoneCategory> getAllZonesCategories() {

        List<MuseumZoneCategory> museumZoneCategories = museumZoneRepository.getAllMuseumZoneCategories();

        if (museumZoneCategories.isEmpty()) {
            throw new AppNotFoundException("Museum Zone Category Not Found");
        }

        return museumZoneCategories;
    }

    @Override
    public MuseumZone createMuseumZone(MuseumZoneRequest museumZoneRequest) {
        return null;
    }
}

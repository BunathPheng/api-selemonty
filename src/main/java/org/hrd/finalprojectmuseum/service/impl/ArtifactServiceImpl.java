package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumArtifactRequest;
import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumArtifact;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.repository.ArtifactRepository;
import org.hrd.finalprojectmuseum.repository.ZoneRepository;
import org.hrd.finalprojectmuseum.service.ArtifactService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtifactServiceImpl implements ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final ZoneRepository zoneRepository;
    LocalDateTime updatedAt = LocalDateTime.now();


    @Override
    public MuseumArtifact createMuseumArtifactByZoneId(MuseumArtifactRequest museumArtifactRequest, UUID museumZoneId, UUID museumId) {
        MuseumZone zone = zoneRepository.retrieveMuseumZoneDetailByZoneId(museumZoneId);
        if (zone == null) {
            throw new AppNotFoundException("Museum zone Id Not Found");
        }
        if (!zone.getMuseumId().equals(museumId)) {
            throw new AppNotFoundException("Insert artifact failed. Zone ID belong to other museum");
        }
        return artifactRepository.createMuseumArtifact(museumArtifactRequest, museumZoneId, updatedAt);
    }

    @Override
    public void updateMuseumArtifactByArtifactId(UUID artifactId, MuseumArtifactRequest museumArtifactRequest, UUID museumId) {
        MuseumArtifact museumArtifact = artifactRepository.retrieveMuseumArtifactByArtifactId(artifactId);
        if (museumArtifact == null) {
            throw new AppNotFoundException("Museum artifact Id Not Found");
        }
        MuseumZone zone = zoneRepository.retrieveMuseumZoneDetailByZoneId(museumArtifact.getZoneId());
        if (zone == null) {
            throw new AppNotFoundException("Museum zone Id Not Found");
        }
        if (!zone.getMuseumId().equals(museumId)) {
            throw new AppNotFoundException("Updated artifact failed. Zone ID belong to other museum");
        }
        artifactRepository.updateMuseumArtifactByArtifactId(artifactId, museumArtifactRequest, updatedAt);
    }

    @Override
    public void deleteMuseumArtifactByArtifactId(UUID artifactId, UUID museumId) {
        MuseumArtifact museumArtifact = artifactRepository.retrieveMuseumArtifactByArtifactId(artifactId);
        if (museumArtifact == null) {
            throw new AppNotFoundException("Museum artifact Id Not Found");
        }
        MuseumZone zone = zoneRepository.retrieveMuseumZoneDetailByZoneId(museumArtifact.getZoneId());
        if (zone == null) {
            throw new AppNotFoundException("Museum zone Id Not Found");
        }
        if (!zone.getMuseumId().equals(museumId)) {
            throw new AppNotFoundException("Deleted artifact failed. Zone ID belong to other museum");
        }
        artifactRepository.deleteMuseumArtifactByArtifactId(artifactId, true);
    }

    @Override
    public MuseumArtifact getMuseumArtifactByArtifactId(UUID artifactId) {
        boolean exist = artifactRepository.retrieveMuseumArtifactId(artifactId);
        if (!exist) {
            throw new AppNotFoundException("Museum artifact Id Not Found");
        }
        return artifactRepository.retrieveMuseumArtifactByArtifactId(artifactId);
    }

    @Override
    public ListResponse<MuseumArtifact> getAllMuseumArtifactByZoneId(UUID zoneId, String search, Integer page, Integer size) {
        search = search == null ? "" : search;
        if (!zoneRepository.retrieveMuseumZoneId(zoneId)){
            throw new AppNotFoundException("Museum zone Id Not Found");
        }
        List<MuseumArtifact> museumArtifacts = artifactRepository.retrieveAllMuseumArtifactsByZoneId(zoneId, search, page, size);
        Integer itemCount = artifactRepository.countArtifact(zoneId, search);
        Pagination pagination = new Pagination();
        return ListResponse.<MuseumArtifact>builder()
                .pagination(pagination.calculatePagination(itemCount, page, size))
                .items(museumArtifacts)
                .build();
    }
}

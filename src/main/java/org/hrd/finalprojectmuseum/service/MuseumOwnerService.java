package org.hrd.finalprojectmuseum.service;

import com.alibaba.fastjson2.JSONObject;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumOwnerRequest;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;

import java.util.UUID;

public interface MuseumOwnerService {
    MuseumOwner getMuseumOwnerByUserId(UUID userId);

    MuseumOwner updateMuseumOwnerByUserId(UUID userId, MuseumOwnerRequest museumOwnerRequest);

    void deleteMuseumOwnerByUserId(UUID userId);

    JSONObject addLanscapeByUserId(UUID userId, JSONObject landscapeRequest);

    void deleteLandscapeByUserId(UUID userId, String landscapeKey);
}

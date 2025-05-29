package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.dto.response.ListResponse;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;

import java.util.UUID;

public interface VisitorService {

    ListResponse<Visitor> getVisitorByUserId(UUID userId, String search, Integer page, Integer size);
}

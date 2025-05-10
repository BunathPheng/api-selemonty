package org.hrd.finalprojectmuseum.service;

import jakarta.validation.Valid;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorRequest;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;

import java.util.UUID;

public interface VisitorService {
    Visitor getProfile(UUID userId);

    Visitor updateVisitor(UUID userId, VisitorRequest visitorRequest);

    void deleteVisitor(UUID userId);
}

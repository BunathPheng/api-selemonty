package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorRequest;
import org.hrd.finalprojectmuseum.model.entity.AppUser;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.repository.AppUserRepository;
import org.hrd.finalprojectmuseum.repository.VisitorRepository;
import org.hrd.finalprojectmuseum.service.VisitorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {
    private final VisitorRepository visitorRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public Visitor getProfile(UUID userId) {
        Visitor visitor = visitorRepository.findVisitor(userId);
        if (visitor == null) {
            throw new AppNotFoundException("Visitor not found");
        }
        return visitor;
    }

    @Override
    public Visitor updateVisitor(UUID userId, VisitorRequest visitorRequest) {
        Visitor visitor = visitorRepository.findVisitor(userId);
        if (visitor == null) {
            throw new AppNotFoundException("Visitor not found");
        }
        visitorRequest.setFullName(visitorRequest.getFullName().isEmpty()? visitor.getFullName() : visitorRequest.getFullName());
        visitorRequest.setContactNumber(visitorRequest.getContactNumber().isEmpty()? visitor.getContactNumber() : visitorRequest.getContactNumber());
        visitorRequest.setDob(visitorRequest.getDob() == null? visitor.getDob() : visitorRequest.getDob());
        visitorRequest.setGender(visitorRequest.getGender().isEmpty()? visitor.getGender() : visitorRequest.getGender());
        visitorRequest.setProfileImageLink(visitorRequest.getProfileImageLink().isEmpty()? visitor.getProfileImageLink() : visitorRequest.getProfileImageLink());
        return visitorRepository.modifyVisitorByVisitorId(userId, visitorRequest);
    }

    @Override
    public void deleteVisitor(UUID userId) {
        AppUser appUser = appUserRepository.getUserById(userId);
        if (appUser == null) {
            throw new AppNotFoundException("UserId is wrong");
        }
        appUserRepository.deleteUser(userId);
    }
}

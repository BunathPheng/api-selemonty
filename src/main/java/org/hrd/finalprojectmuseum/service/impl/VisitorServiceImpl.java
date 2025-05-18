package org.hrd.finalprojectmuseum.service.impl;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.exception.AppNotFoundException;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorRequest;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.repository.AppUserRepository;
import org.hrd.finalprojectmuseum.repository.VisitorRepository;
import org.hrd.finalprojectmuseum.service.VisitorService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hrd.finalprojectmuseum.utils.RequestUtils.getOrDefault;

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
    public Visitor updateVisitor(UUID userId, VisitorRequest request) {
        Visitor visitor = visitorRepository.findVisitor(userId);
        if (visitor == null) {
            throw new AppNotFoundException("Visitor not found");
        }

        VisitorRequest updatedRequest = new VisitorRequest();

        updatedRequest.setFullName(getOrDefault(request.getFullName(), visitor.getFullName()));
        updatedRequest.setContactNumber(getOrDefault(request.getContactNumber(), visitor.getContactNumber()));
        updatedRequest.setGender(getOrDefault(request.getGender(), visitor.getGender()));
        updatedRequest.setProfileImageLink(getOrDefault(request.getProfileImageLink(), visitor.getProfileImageLink()));
        updatedRequest.setDob(request.getDob()!=null?request.getDob():visitor.getDob());
        return visitorRepository.modifyVisitorByVisitorId(userId, updatedRequest, LocalDateTime.now());
    }


    @Override
    public void deleteVisitor(UUID userId) {
        AppUserRegister appUser = appUserRepository.getUserById(userId);
        if (appUser == null) {
            throw new AppNotFoundException("UserId is wrong");
        }
        appUserRepository.deleteUser(userId);
    }
}

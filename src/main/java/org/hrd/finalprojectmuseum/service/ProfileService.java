package org.hrd.finalprojectmuseum.service;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.Valid;
import org.hrd.finalprojectmuseum.model.dto.request.PaymentAccountRequest;
import org.hrd.finalprojectmuseum.model.dto.request.admin.AdminRequest;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumOwnerRequest;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorRequest;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumCategory;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;

import java.util.List;
import java.util.UUID;

public interface ProfileService {

    MuseumOwner getMuseumOwnerByUserId(UUID userId);

    MuseumOwner updateMuseumOwnerByUserId(UUID userId, MuseumOwnerRequest museumOwnerRequest);

    void deleteMuseumOwnerByUserId(UUID userId);

    JSONObject addLanscapeByUserId(UUID userId, JSONObject landscapeRequest);

    void deleteLandscapeByUserId(UUID userId, String landscapeKey);

    List<MuseumCategory> getMuseumCategories();

    MuseumOwner updateMuseumOwnerPaymentByUserId(UUID userId, PaymentAccountRequest paymentAccountRequest);

    Admin getAdminByUserId(UUID userId);

    Admin updateAdminByUserId(UUID userId, AdminRequest adminRequest);

    Visitor getProfile(UUID userId);

    Visitor updateVisitor(UUID userId, VisitorRequest visitorRequest);

    void deleteVisitor(UUID userId);
}

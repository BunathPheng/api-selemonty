package org.hrd.finalprojectmuseum.service;

import jakarta.validation.Valid;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.*;
import org.hrd.finalprojectmuseum.model.dto.request.PaymentAccountRequest;
import org.hrd.finalprojectmuseum.model.dto.request.admin.AdminRequest;
import org.hrd.finalprojectmuseum.model.dto.request.visitor.VisitorRequest;
import org.hrd.finalprojectmuseum.model.entity.PaymentCredential;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumCategory;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;

import java.util.List;
import java.util.UUID;

public interface ProfileService {

    MuseumOwner getMuseumOwnerByUserId(UUID userId);

    MuseumOwner updateMuseumOwnerByUserId(UUID userId, MuseumOwnerRequest museumOwnerRequest);

    List<MuseumCategory> getMuseumCategories();

    PaymentCredential updateMuseumOwnerPaymentByUserId(UUID userId, PaymentAccountRequest paymentAccountRequest);

    Admin getAdminByUserId(UUID userId);

    Admin updateAdminByUserId(UUID userId, AdminRequest adminRequest);

    Visitor getProfile(UUID userId);

    Visitor updateVisitor(UUID userId, VisitorRequest visitorRequest);

    void deleteVisitor(UUID userId);

    UUID getMuseumIdByUserId();

    PaymentCredential getMuseumPaymentCredential(UUID museumId);

    MuseumOwner updateMuseumAboutDetailByMuseumId(UUID museumId, MuseumAboutRequest museumAboutRequest);

    MuseumOwner updateMuseumContactByMuseumId(UUID museumId, MuseumContactRequest museumContactRequest);

    MuseumOwner updateMuseumlandscapeByMuseumId(UUID museumId, LandscapeRequest landscapeRequest);

    MuseumOwner updateMuseumBannerByMuseumId(UUID museumId, BannerRequest bannerRequest);

    MuseumOwner updateMuseumLogoByMuseumId(UUID museumId, LogoRequest logoRequest);

    MuseumOwner updateMuseumLocationByMuseumId(UUID museumId, MuseumLocationRequest museumLocationRequest);
}

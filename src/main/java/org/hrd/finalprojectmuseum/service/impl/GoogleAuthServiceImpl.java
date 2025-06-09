package org.hrd.finalprojectmuseum.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.jwt.JwtUtils;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.LoginToken;
import org.hrd.finalprojectmuseum.model.entity.admin.Admin;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumOwner;
import org.hrd.finalprojectmuseum.model.entity.visitor.Visitor;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.repository.AppUserRepository;
import org.hrd.finalprojectmuseum.service.GoogleAuthService;
import org.hrd.finalprojectmuseum.service.ProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final ProfileService profileService;
    @Value("${app.google.client-id}")
    private String webClientId;
    private final HttpTransport transport = new NetHttpTransport();
    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private final AppUserRepository appUserRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginToken<?>
    verifyAndExtractUserInfo(String idTokenString, String role) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(webClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            Payload payload = idToken.getPayload();
            System.out.println((String) payload.get("name"));
            AppUserRegister appUserRegister = appUserRepository.findUserByEmail(payload.getEmail());
            if(appUserRegister == null) {
                String encodedPass = passwordEncoder.encode("Kom@3");
                Role roleEnum = role.equals("VISITOR") ? Role.ROLE_VISITOR : Role.ROLE_MUSEUM_OWNER;
                System.out.println(roleEnum);
                AppUserRegister registerUser = appUserRepository.registerUser(payload.getEmail(), encodedPass, roleEnum, true);
                LoginToken<?> loginToken;
                if (role.equals("VISITOR")){
                    appUserRepository.storeVisitor(registerUser.getUserId(), (String) payload.get("name"), (String) payload.get("picture"));
                    Visitor visitor = profileService.getProfile(registerUser.getUserId());
                    loginToken = LoginToken.<Visitor>builder()
                            .token(jwtUtils.generateToken(registerUser.getEmail(), registerUser.getUserId(), registerUser.getRole().toString()))
                            .user(visitor)
                            .build();
                }else {
                    System.out.println(roleEnum);
                    appUserRepository.storeMeseumOwner(registerUser.getUserId(), (String) payload.get("name"), (String) payload.get("picture"), null, null, null, null);
                    MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(registerUser.getUserId());
                    loginToken = LoginToken.<MuseumOwner>builder()
                            .token(jwtUtils.generateToken(registerUser.getEmail(), registerUser.getUserId(), registerUser.getRole().toString()))
                            .user(museumOwner)
                            .build();
                }

                return loginToken;
            }else {
                LoginToken<?> loginToken;
                if (appUserRegister.getRole() == Role.ROLE_ADMIN) {
                    Admin admin = profileService.getAdminByUserId(appUserRegister.getUserId());
                    loginToken = LoginToken.<Admin>builder()
                            .token(jwtUtils.generateToken(appUserRegister.getEmail(), appUserRegister.getUserId(), String.valueOf(appUserRegister.getRole())))
                            .user(admin)
                            .build();
                }
                else if (appUserRegister.getRole() == Role.ROLE_MUSEUM_OWNER) {
                    MuseumOwner museumOwner = profileService.getMuseumOwnerByUserId(appUserRegister.getUserId());
                    loginToken = LoginToken.<MuseumOwner>builder()
                            .token(jwtUtils.generateToken(appUserRegister.getEmail(), appUserRegister.getUserId(), String.valueOf(appUserRegister.getRole())))
                            .user(museumOwner)
                            .build();
                }
                else {
                    Visitor visitor = profileService.getProfile(appUserRegister.getUserId());
                    loginToken = LoginToken.<Visitor>builder()
                            .token(jwtUtils.generateToken(appUserRegister.getEmail(), appUserRegister.getUserId(), String.valueOf(appUserRegister.getRole())))
                            .user(visitor)
                            .build();
                }
                return loginToken;
            }
        } else {
            throw new IllegalArgumentException("Invalid ID token.");
        }
    }
}

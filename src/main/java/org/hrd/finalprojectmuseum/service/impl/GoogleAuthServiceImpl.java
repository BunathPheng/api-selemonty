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
import org.hrd.finalprojectmuseum.model.entity.AppUser;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.LoginToken;
import org.hrd.finalprojectmuseum.model.enums.Role;
import org.hrd.finalprojectmuseum.repository.AppUserRepository;
import org.hrd.finalprojectmuseum.service.GoogleAuthService;
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

    @Value("${app.google.client-id}")
    private String webClientId;
    private final HttpTransport transport = new NetHttpTransport();
    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private final AppUserRepository appUserRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginToken verifyAndExtractUserInfo(String idTokenString, String role) throws GeneralSecurityException, IOException {
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
                AppUserRegister registerUser = appUserRepository.registerUser(payload.getEmail(), encodedPass, roleEnum, true);
                if (role.equals("VISITOR")){
                    appUserRepository.storeVisitor(registerUser.getUserId(), (String) payload.get("name"), (String) payload.get("picture"));
                }else {
                    appUserRepository.storeMeseumOwner(registerUser.getUserId(), (String) payload.get("name"), (String) payload.get("picture"), null, null, null);
                }
                return new LoginToken(jwtUtils.generateToken(registerUser.getEmail(), registerUser.getUserId(), registerUser.getRole().toString()));
            }else {
                return new LoginToken(jwtUtils.generateToken(appUserRegister.getEmail(), appUserRegister.getUserId(), appUserRegister.getRole().toString()));
            }
        } else {
            throw new IllegalArgumentException("Invalid ID token.");
        }
    }
}

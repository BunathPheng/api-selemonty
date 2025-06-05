//package org.hrd.finalprojectmuseum.security;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.hrd.finalprojectmuseum.jwt.JwtUtils;
//import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
//import org.hrd.finalprojectmuseum.model.enums.Role;
//import org.hrd.finalprojectmuseum.repository.AppUserRepository;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    @Value("${app.frontend.url}")
//    private String frontendUrl;
//
//    private final JwtUtils jwtUtils;
//    private final AppUserRepository appUserRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException, ServletException {
//
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//
//        // Extract user information from OAuth2User
//        String email = oAuth2User.getAttribute("email");
//        String name = oAuth2User.getAttribute("name");
//        String pictureUrl = oAuth2User.getAttribute("picture");
//
//        // Get the requested role from session or parameter
//        // Default to ROLE_VISITOR if not specified
//        String roleParam = request.getParameter("role");
//        Role role = (roleParam != null && roleParam.equalsIgnoreCase("MUSEUM-OWNER"))
//                ? Role.ROLE_MUSEUM_OWNER
//                : Role.ROLE_VISITOR;
//
//        // Check if user exists in the database
//        AppUserRegister appUser = appUserRepository.findUserByEmail(email);
//
//        if (appUser == null) {
//            // Create new user
//            String encodedPass = passwordEncoder.encode("Kom@3"); // Default password or use a random one
//            AppUserRegister registerUser = appUserRepository.registerUser(email, encodedPass, role, true);
//
//            if (role == Role.ROLE_VISITOR) {
//                appUserRepository.storeVisitor(registerUser.getUserId(), name, pictureUrl);
//            } else {
//                appUserRepository.storeMeseumOwner(registerUser.getUserId(), name, pictureUrl, address, null, null, null);
//            }
//
//            // Generate JWT token
//            String token = jwtUtils.generateToken(registerUser.getEmail(), registerUser.getUserId(), registerUser.getRole().toString());
//            redirectWithToken(request, response, token);
//        } else {
//            // User exists, generate token
//            String token = jwtUtils.generateToken(appUser.getEmail(), appUser.getUserId(), appUser.getRole().toString());
//            redirectWithToken(request, response, token);
//        }
//    }
//
//    private void redirectWithToken(HttpServletRequest request, HttpServletResponse response, String token)
//            throws IOException {
//        // Build redirect URL with token
//        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
//                .queryParam("token", token)
//                .build().toUriString();
//
//        // Redirect to frontend with token
//        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
//    }
//}
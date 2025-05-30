
package org.hrd.finalprojectmuseum.config;

import lombok.RequiredArgsConstructor;
import org.hrd.finalprojectmuseum.jwt.CustomAccessDeniedHandler;
import org.hrd.finalprojectmuseum.jwt.JwtAuthEntryPoint;
import org.hrd.finalprojectmuseum.jwt.JwtAuthFilter;
import org.hrd.finalprojectmuseum.jwt.JwtUtils;
import org.hrd.finalprojectmuseum.security.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@SuppressWarnings("ALL")
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtils jwtUtils) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(
                                "/api/v1/auths/**",
                                "/api/v1/oauth2/**",
                                "/api/v1/museum/categories/**",
                                "/api/v1/file/**",
                                "/auth/**",
                                "/oauth2/**",
                                "/api/v1/files/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/v1/museum/all"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                // Museum endpoints
                                "/api/v1/museum/**",
                                "/api/v1/museum/nearby",

                                // Event endpoints
                                "/api/v1/events",
                                "/api/v1/events/**",

                                // Artifact endpoints (public read access)
                                "/api/v1/artifacts",
                                "/api/v1/artifacts/**",

                                // Review endpoints
                                "/api/v1/reviews",
                                "/api/v1/reviews/**",

                                // Zone endpoints
                                "/api/v1/zone",
                                "/api/v1/zone/**",

                                //Schedule endpoints
                                "/api/v1/museum/schedule/**",
                                "/api/v1/museum/schedule/grouped/**",
                                "/api/v1/museum/schedule/detail/**",

                                //Ticket
                                "/api/v1/tickets/**"
                        ).permitAll()
//                        .requestMatchers("/api/v1x    /artifact/**").hasRole("MUSEUM_OWNER")
//                        .requestMatchers(HttpMethod.POST, "/api/v1/booking/*").hasRole("VISITOR")  // Only POST booking creation for visitors
//                        .requestMatchers(HttpMethod.GET, "/api/v1/booking").hasAnyRole("VISITOR", "MUSEUM_OWNER")  // GET booking history for both
//                        .requestMatchers(HttpMethod.GET, "/api/v1/booking/*").hasAnyRole("VISITOR", "MUSEUM_OWNER")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Add OAuth2 login configuration
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint()
                        .baseUri("/oauth2/authorize")
                        .and()
                        .redirectionEndpoint()
                        .baseUri("/oauth2/callback/*")
                        .and()
                        .userInfoEndpoint()
                        .userService(oauth2UserService())
                        .and()
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return userRequest -> delegate.loadUser(userRequest);
    }
}
package org.hrd.finalprojectmuseum.config;

import org.hrd.finalprojectmuseum.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    if (jwtUtils.validateToken(token)) {
                        String email = jwtUtils.extractEmail(token);
                        String userId = jwtUtils.extractUserId(token);
                        List<String> roles = jwtUtils.extractRoles(token);

                        List<GrantedAuthority> authorities = roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userId,
                                        null,
                                        authorities
                                );

                        accessor.setUser(auth);

                        Objects.requireNonNull(accessor.getSessionAttributes()).put("email", email);
                        accessor.getSessionAttributes().put("userId", userId);
                        accessor.getSessionAttributes().put("roles", roles);

                    } else {
                        throw new RuntimeException("Invalid token");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Invalid token: " + e.getMessage());
                }
            } else {
                throw new RuntimeException("No Authorization header found");
            }
        }
        return message;
    }
}
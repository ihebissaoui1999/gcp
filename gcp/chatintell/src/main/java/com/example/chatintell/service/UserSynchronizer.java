package com.example.chatintell.service;

import com.example.chatintell.entity.Role;
import com.example.chatintell.entity.User;
import com.example.chatintell.repository.RoleRepository;
import com.example.chatintell.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSynchronizer {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
       final ConcurrentHashMap<String, Instant> lastSync = new ConcurrentHashMap<>();
    public void synchronizeWithIdp(Jwt token) {
        String userIdFromToken = token.getSubject();
        Instant now = Instant.now();

        Instant last = lastSync.get(userIdFromToken);
        if (last != null && last.isAfter(now.minus(Duration.ofMinutes(2)))) {
            log.debug("Skip sync for user {} (last sync at {})", userIdFromToken, last);
            return;
        }

        log.info("Synchronizing user with idp");
        getUserEmail(token).ifPresent(userEmail -> {
            log.info("Synchronizing user having email {}", userEmail);

           // String userIdFromToken = token.getSubject(); // typiquement le sub de Keycloak = uuid

            synchronized (this) { // bloque les threads simultanés sur cette méthode
                Optional<User> optUser = userRepository.findById(userIdFromToken);

                User user = userMapper.fromTokenAttributes(token.getClaims());
                user.setUserid(userIdFromToken); // forcer le bon ID du token

                if (optUser.isPresent()) {
                    User existingUser = optUser.get();

                    // on conserve les propriétés non mises à jour par l'IDP si nécessaire
                    user.setRoles(existingUser.getRoles());
                    user.setRewards(existingUser.getRewards());
                    user.setChatsAsSender(existingUser.getChatsAsSender());
                    user.setChatsAsRecipient(existingUser.getChatsAsRecipient());
                    user.setTickets(existingUser.getTickets());
                    user.setMatiriels(existingUser.getMatiriels());
                    user.setCreatedDate(existingUser.getCreatedDate());
                }

                List<String> roleNames = extractRoles(token);
                Role role = null;

                if (!roleNames.isEmpty()) {
                    String roleName = roleNames.get(0);
                    role = roleRepository.findByName(roleName)
                            .orElseGet(() -> {
                                Role newRole = new Role();
                                newRole.setName(roleName);
                                return roleRepository.save(newRole);
                            });
                }

                user.setRoles(role);
                userRepository.save(user);
            }
        });
    }



    private Optional<String> getUserEmail(Jwt token) {
        Map<String, Object> attributes = token.getClaims();
        return Optional.ofNullable(attributes.get("email")).map(Object::toString);
    }

    private List<String> extractRoles(Jwt jwt) {
        List<String> roles = new ArrayList<>();
        Set<String> excludedRoles = Set.of("offline_access", "uma_authorization", "default_roles_bnns");

        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> realmRoles = (List<String>) realmAccess.get("roles");
            roles.addAll(realmRoles);
        }

        Map<String, Object> resourceAccess = (Map<String, Object>) jwt.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey("chat-app")) {
            Map<String, Object> client = (Map<String, Object>) resourceAccess.get("chat-app");
            if (client.containsKey("roles")) {
                List<String> clientRoles = (List<String>) client.get("roles");
                roles.addAll(clientRoles);
            }
        }

        return roles.stream()
                .map(role -> role.replace("-", "_"))
                .filter(role -> !excludedRoles.contains(role))
                .collect(Collectors.toList());
    }

}

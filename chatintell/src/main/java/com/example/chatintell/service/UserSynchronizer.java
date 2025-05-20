package com.example.chatintell.service;

import com.example.chatintell.entity.Role;
import com.example.chatintell.entity.User;
import com.example.chatintell.repository.RoleRepository;
import com.example.chatintell.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSynchronizer {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    public void synchronizeWithIdp(Jwt token) {
        log.info("Synchronizing user with idp");
        getUserEmail(token).ifPresent(userEmail -> {
            log.info("Synchronizing user having email {}", userEmail);

            Optional<User> optUser = userRepository.findByEmail(userEmail);
            User user = userMapper.fromTokenAttributes(token.getClaims());
            optUser.ifPresent(value -> user.setUserid(value.getUserid()));

            List<String> roleNames = extractRoles(token);
            Role role = null;

            if (!roleNames.isEmpty()) {
                String roleName = roleNames.get(0); // Prend le premier rôle de la liste
                role = roleRepository.findByName(roleName)
                        .orElseGet(() -> {
                            Role newRole = new Role();
                            newRole.setName(roleName);
                            return roleRepository.save(newRole);
                        });
            }

            user.setRoles(role);
            userRepository.save(user);
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


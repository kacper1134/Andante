package org.andante.config.security.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum KeycloakRole {
    BLOGGER("blogger"),
    ADMIN("admin");

    private final String name;
}

package org.andante.config.security.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class KeycloakRealmRoleConverter implements Converter<Jwt, Flux<GrantedAuthority>> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES = "roles";

    @Override
    @SuppressWarnings("unchecked")
    public Flux<GrantedAuthority> convert(Jwt source) {
        Map<String, Object> realmAccess = (Map<String, Object>) source.getClaims().get(REALM_ACCESS);

        if (realmAccess == null) {
            return Flux.empty();
        }

        List<GrantedAuthority> grantedAuthorities = ((List<String>)realmAccess.get(ROLES)).stream()
                .map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return Flux.fromIterable(grantedAuthorities);
    }
}

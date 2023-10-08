package org.andante.activity.logic;

import org.andante.activity.dto.UpdatedUserDetailsDTO;
import org.keycloak.representations.account.UserRepresentation;
import reactor.core.publisher.Mono;


public interface ProfileService {
    Mono<Void> updateUser(String userId, String userName, UpdatedUserDetailsDTO updatedUserDetailsDTO);
    Mono<UserRepresentation> getUserDetails(String userId);
}

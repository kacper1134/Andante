package org.andante.activity.logic.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.andante.activity.dto.UpdatedUserDetailsDTO;
import org.andante.activity.logic.ProfileService;
import org.andante.activity.logic.UserProfileService;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DefaultProfileService implements ProfileService {
    private final UserProfileService userProfileService;
    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak_admin_token_path}")
    private String keycloakAdminTokenPath;

    @Value("${keycloak_update_user_path}")
    private String keycloakUpdateUserPath;

    @Value("${keycloak_get_user_path}")
    private String keycloakGetUserPath;

    @Value("${keycloak_admin_username}")
    private String adminUsername;

    @Value("${keycloak_admin_password}")
    private String adminPassword;

    @Override
    @Transactional
    public Mono<Void> updateUser(String userId, String userName, UpdatedUserDetailsDTO updatedUserDetailsDTO) {
        userProfileService.setUserImage(userId, userName, updatedUserDetailsDTO.getProfileImageUrl());
        return makeUpdateRequest(userId, updatedUserDetailsDTO);
    }

    @Override
    @Transactional
    public Mono<UserRepresentation> getUserDetails(String userId) {
        return makeUserRepresentationRequest(userId);
    }

    private Mono<UserRepresentation> makeUserRepresentationRequest(String userId) {
        WebClient client = WebClient.builder()
                .baseUrl(keycloakUrl)
                .build();

        return getAdminToken(client).flatMap(token -> fetchUserDetails(token, userId, client));
    }

    private Mono<Void> makeUpdateRequest(String userId, UpdatedUserDetailsDTO updatedUserDetailsDTO) {
        WebClient client = WebClient.builder()
                .baseUrl(keycloakUrl)
                .build();

        return getAdminToken(client).flatMap(token -> updateUserDetails(token, userId, updatedUserDetailsDTO, client)).then();
    }

    private Mono<String> getAdminToken(WebClient client) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", adminUsername);
        body.add("password", adminPassword);
        body.add("client_id", "admin-cli");
        body.add("grant_type", "password");

        return client.post()
                .uri(keycloakAdminTokenPath)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> response.get("access_token"))
                .cast(String.class);
    }

    private Mono<Void> updateUserDetails(String token, String userId,
                                         UpdatedUserDetailsDTO updatedUserDetailsDTO, WebClient client) {
        return client.put()
                .uri(keycloakUpdateUserPath + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userDetailsDtoToBody(updatedUserDetailsDTO)), Map.class)
                .header(HttpHeaders.AUTHORIZATION, "bearer " + token)
                .retrieve()
                .bodyToMono(Void.class);
    }

    private Mono<UserRepresentation> fetchUserDetails(String token, String userId, WebClient client) {
        return client.get()
                .uri(keycloakGetUserPath + userId)
                .header(HttpHeaders.AUTHORIZATION, "bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserRepresentation.class);
    }

    @SneakyThrows
    private Map<String, Object> userDetailsDtoToBody(UpdatedUserDetailsDTO updatedUserDetailsDTO) {
        Map<String, Object> body = new HashMap<>();
        Map<String, String> customAttributes = new HashMap<>();

        customAttributes.put("country", updatedUserDetailsDTO.getCountry());
        customAttributes.put("gender", updatedUserDetailsDTO.getGender());
        customAttributes.put("city", updatedUserDetailsDTO.getCity());
        customAttributes.put("street", updatedUserDetailsDTO.getStreet());
        customAttributes.put("postalcode", updatedUserDetailsDTO.getPostalCode());
        customAttributes.put("phonenumber", updatedUserDetailsDTO.getPhoneNumber());
        customAttributes.put("dateOfBirth", updatedUserDetailsDTO.getDateOfBirth());
        customAttributes.put("description", updatedUserDetailsDTO.getDescription());

        body.put("firstName", updatedUserDetailsDTO.getFirstName());
        body.put("lastName", updatedUserDetailsDTO.getLastName());
        body.put("attributes", customAttributes);
        return body;
    }
}

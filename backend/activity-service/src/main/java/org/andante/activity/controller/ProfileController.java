package org.andante.activity.controller;

import lombok.RequiredArgsConstructor;
import org.andante.activity.controller.decoder.JWTTokenDecoder;
import org.andante.activity.controller.mapper.UserProfileDTOModelMapper;
import org.andante.activity.dto.UpdatedUserDetailsDTO;
import org.andante.activity.dto.UserImageDTO;
import org.andante.activity.dto.UserProfileDTO;
import org.andante.activity.logic.ProfileService;
import org.andante.activity.logic.UserProfileService;
import org.andante.activity.logic.model.UserProfile;
import org.andante.enums.OperationStatus;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/profile")
@Validated
public class ProfileController {

    private static final String IMAGE_URL_BLANK_ERROR_MESSAGE = "Image address must not be blank";
    private static final String USERNAME_BLANK_ERROR_MESSAGE = "Username must not be blank";
    private static final String USERNAME_NULL_ERROR_MESSAGE = "Username must not be null";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "Identifiers list must not be null";
    private static final String IDENTIFIERS_LIST_SIZE_ERROR_MESSAGE = "Identifiers list must contain at least {min} element(s)";

    private final JWTTokenDecoder jwtTokenDecoder;
    private final ProfileService profileService;
    private final UserProfileService userProfileService;
    private final UserProfileDTOModelMapper userProfileMapper;

    @PutMapping
    public ResponseEntity<Mono<Void>> updateUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                 @Valid @RequestBody UpdatedUserDetailsDTO updatedUserDetailsDTO) {
        String userId = jwtTokenDecoder.decode(authorizationHeader).getClaim("sub");
        String userName = jwtTokenDecoder.decode(authorizationHeader).getClaim("preferred_username");
        return ResponseEntity.ok(profileService.updateUser(userId, userName, updatedUserDetailsDTO));
    }

    @GetMapping("/details/{username}")
    public ResponseEntity<Mono<UserRepresentation>> getUserDetails(@NotBlank(message = USERNAME_BLANK_ERROR_MESSAGE)
                                                                   @PathVariable("username") String username) {
        UserProfile userProfile = userProfileService.getUserProfile(username);

        Mono<UserRepresentation> userRepresentation = profileService.getUserDetails(userProfile.getKey());

        return ResponseEntity.ok(userRepresentation);
    }

    @GetMapping
    public ResponseEntity<UserProfileDTO> getProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String userId = jwtTokenDecoder.decode(authorizationHeader).getClaim("sub");
        String userName = jwtTokenDecoder.decode(authorizationHeader).getClaim("preferred_username");

        UserProfile serviceResponse = userProfileService.getUserProfile(userId, userName);

        UserProfileDTO userProfile = userProfileMapper.toDTO(serviceResponse);

        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/{username}/observers")
    public ResponseEntity<Set<UserProfileDTO>> getObservers(@NotBlank(message = USERNAME_BLANK_ERROR_MESSAGE)
                                                            @PathVariable("username") String username) {
        Set<UserProfile> serviceResponse = userProfileService.getObservers(username);

        Set<UserProfileDTO> observers = serviceResponse.stream()
                .map(userProfileMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(observers);
    }

    @GetMapping("/{username}/observing")
    public ResponseEntity<Set<UserProfileDTO>> getObserving(@NotBlank(message = USERNAME_BLANK_ERROR_MESSAGE)
                                                            @PathVariable("username") String username) {
        Set<UserProfile> serviceResponse = userProfileService.getObserving(username);

        Set<UserProfileDTO> observing = serviceResponse.stream()
                .map(userProfileMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(observing);
    }

    @PostMapping("/observation")
    public ResponseEntity<OperationStatus> changeObservationStatus(@NotBlank(message = USERNAME_BLANK_ERROR_MESSAGE) @RequestParam("observed") String observed,
                                                                   @NotBlank(message = USERNAME_BLANK_ERROR_MESSAGE) @RequestParam("observing") String observer) {
        userProfileService.changeObservationStatus(observed, observer);

        return ResponseEntity.ok()
                .build();
    }

    @PostMapping("/image")
    public ResponseEntity<UserProfile> setProfileImage(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                       @RequestBody @NotBlank(message = IMAGE_URL_BLANK_ERROR_MESSAGE) String imageUrl) {
        String userId = jwtTokenDecoder.decode(authorizationHeader).getClaim("sub");
        String userName = jwtTokenDecoder.decode(authorizationHeader).getClaim("preferred_username");
        return ResponseEntity.ok(userProfileService.setUserImage(userId, userName, imageUrl));
    }
    @GetMapping("/image")
    public ResponseEntity<List<UserImageDTO>> getUsersImage(@RequestParam("usernames") @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) @Size(min = 1, message = IDENTIFIERS_LIST_SIZE_ERROR_MESSAGE)
                                                                List<@NotBlank(message = USERNAME_BLANK_ERROR_MESSAGE) String> usernames) {
        return ResponseEntity.ok(userProfileService.getUsersImage(usernames));
    }

    @PostMapping("/image/community")
    public ResponseEntity<UserProfile> setCommunityProfileImage(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
                                                                @RequestBody @NotBlank(message = IMAGE_URL_BLANK_ERROR_MESSAGE) String imageUrl) {
        String userId = jwtTokenDecoder.decode(authorizationHeader).getClaim("sub");
        String userName = jwtTokenDecoder.decode(authorizationHeader).getClaim("preferred_username");
        return ResponseEntity.ok(userProfileService.setUserCommunityImage(userId, userName, imageUrl));
    }

    @GetMapping("/image/community")
    public ResponseEntity<UserImageDTO> getUserCommunityImage(@RequestParam("username") @NotNull(message = USERNAME_NULL_ERROR_MESSAGE) String username) {
        return ResponseEntity.ok(userProfileService.getUserCommunityImage(username));
    }
}

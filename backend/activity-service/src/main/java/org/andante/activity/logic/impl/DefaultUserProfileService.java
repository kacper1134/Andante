package org.andante.activity.logic.impl;

import lombok.RequiredArgsConstructor;
import org.andante.activity.dto.UserImageDTO;
import org.andante.activity.exception.UserConflictException;
import org.andante.activity.exception.UserNotFoundException;
import org.andante.activity.logic.UserProfileService;
import org.andante.activity.logic.mapper.UserProfileModelEntityMapper;
import org.andante.activity.logic.model.UserProfile;
import org.andante.activity.repository.UserProfileRepository;
import org.andante.activity.repository.entity.UserProfileEntity;
import org.andante.enums.OperationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DefaultUserProfileService implements UserProfileService {

    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User %s does not exist";
    private static final String USER_CONFLICT_EXCEPTION_MESSAGE = "User cannot observe themselves";

    private final UserProfileRepository userProfileRepository;
    private final UserProfileModelEntityMapper userProfileModelEntityMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserProfile getUserProfile(String username) {
        Optional<UserProfileEntity> profileEntity = userProfileRepository.findByUsername(username);

        if (profileEntity.isEmpty()) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, username));
        }

        return userProfileModelEntityMapper.toModel(profileEntity.get());
    }


    @Override
    @Transactional
    public UserProfile getUserProfile(String userId, String userName) {
        Optional<UserProfileEntity> profileEntityOptional = userProfileRepository.findById(userId);

        if(profileEntityOptional.isPresent()) {
            return userProfileModelEntityMapper.toModel(profileEntityOptional.get());
        }

        Optional<UserProfileEntity> profileByUsername = userProfileRepository.findByUsername(userName);
        profileByUsername.ifPresent(userProfileEntity -> userProfileRepository.deleteById(userProfileEntity.getId()));
        UserProfileEntity userProfileEntity = new UserProfileEntity(userId, userName, "", "",
                new HashSet<>(), new HashSet<>());
        userProfileRepository.save(userProfileEntity);
        return userProfileModelEntityMapper.toModel(userProfileEntity);

    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<UserProfile> getObservers(String username) {
        Optional<UserProfileEntity> profileEntity = userProfileRepository.findByUsername(username);

        if (profileEntity.isEmpty()) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, username));
        }

        return profileEntity.get().getObservers().stream()
                .map(userProfileModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public Set<UserProfile> getObserving(String username) {
        Optional<UserProfileEntity> profileEntity = userProfileRepository.findByUsername(username);

        if (profileEntity.isEmpty()) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, username));
        }

        return profileEntity.get().getObserved().stream()
                .map(userProfileModelEntityMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public OperationStatus changeObservationStatus(String username, String observerUsername) {
        if (username.equals(observerUsername)) {
            throw new UserConflictException(USER_CONFLICT_EXCEPTION_MESSAGE);
        }

        Optional<UserProfileEntity> observed = userProfileRepository.findByUsername(username);

        if (observed.isEmpty()) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, username));
        }

        Optional<UserProfileEntity> observer = userProfileRepository.findByUsername(observerUsername);

        if (observer.isEmpty()) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND_EXCEPTION_MESSAGE, observerUsername));
        }

        UserProfileEntity observedProfile = observed.get();
        UserProfileEntity observerProfile = observer.get();

        if (!observedProfile.getObservers().contains(observerProfile)) {
            observedProfile.getObservers().add(observerProfile);
        } else {
            observedProfile.getObservers().remove(observerProfile);
        }

        userProfileRepository.save(observedProfile);

        return OperationStatus.OK;
    }

    @Override
    @Transactional
    public UserProfile setUserImage(String userId, String userName, String imageUrl) {
        UserProfile userProfile = getUserProfile(userId, userName);
        userProfile.setImageUrl(imageUrl);
        UserProfileEntity userProfileEntity = userProfileRepository.save(userProfileModelEntityMapper.toEntity(userProfile));
        return userProfileModelEntityMapper.toModel(userProfileEntity);
    }

    @Override
    public List<UserImageDTO> getUsersImage(List<String> userNames) {
        List<UserProfileEntity> userProfileEntities = userProfileRepository.findAllByUsernameIsIn(userNames);
        return userProfileEntities.stream().map(profile ->
                new UserImageDTO(profile.getUsername(), profile.getImageUrl())).collect(Collectors.toList());
    }

    @Override
    public UserImageDTO getUserCommunityImage(String username) {
        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findByUsername(username);
        return new UserImageDTO(username, userProfileEntity.map(UserProfileEntity::getCommunityImageUrl).orElse(""));
    }

    @Override
    @Transactional
    public UserProfile setUserCommunityImage(String userId, String userName, String imageUrl) {
        UserProfile userProfile = getUserProfile(userId, userName);
        userProfile.setCommunityImageUrl(imageUrl);
        UserProfileEntity userProfileEntity = userProfileRepository.save(userProfileModelEntityMapper.toEntity(userProfile));
        return userProfileModelEntityMapper.toModel(userProfileEntity);
    }
}

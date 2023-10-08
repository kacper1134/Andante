package org.andante.forum.controller;

import dto.post.*;
import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.forum.controller.mapper.PostDTOModelMapper;
import org.andante.forum.controller.mapper.PostLikesDTOModelMapper;
import org.andante.forum.logic.model.post.PostInputModel;
import org.andante.forum.logic.model.post.PostLikesRelationModel;
import org.andante.forum.logic.model.post.PostOutputModel;
import org.andante.forum.logic.service.impl.PostServiceImpl;
import org.andante.mappers.OperationHttpStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/forum/post", produces = MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PostController {

    private static final String IDENTIFIER_ERROR_MESSAGE = "Product identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Product identifier must not be a null";
    private static final String EMAIL_BLANK_ERROR_MESSAGE = "User email address must not be blank";
    private static final String EMAIL_NOT_VALID_ERROR_MESSAGE = "User email '${validatedValue}' must be an email address";

    private final PostServiceImpl postService;
    private final PostDTOModelMapper postDTOModelMapper;
    private final PostLikesDTOModelMapper postLikesDTOModelMapper;
    private final OperationHttpStatusMapper operationHttpStatusMapper;

    @GetMapping
    public ResponseEntity<PostOutputDTO> get(
            @RequestParam("id")
            @Positive(message = IDENTIFIER_ERROR_MESSAGE)
            @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE)
            Long id
    ) {
        PostOutputModel postOutputModel = postService.getPost(id);
        PostOutputDTO postOutputDTO = postDTOModelMapper.toDTO(postOutputModel);
        return ResponseEntity
                .ok()
                .body(postOutputDTO);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<PostOutputDTO>> getPage(
            @Valid
            PostQuerySpecification postQuerySpecification
    ) {
        Page<PostOutputModel> postOutputModels = postService.getByQuery(postQuerySpecification);
        Page<PostOutputDTO> postOutputDTOs = postOutputModels.map(postDTOModelMapper::toDTO);
        return ResponseEntity.ok().body(postOutputDTOs);
    }

    @GetMapping("/top")
    public ResponseEntity<List<PostOutputDTO>> getTopPage(
            @Valid
            TopQuerySpecification topQuerySpecification
    ) {
        List<PostOutputModel> postOutputModels = postService.getTopPage(topQuerySpecification);
        List<PostOutputDTO> postOutputDTOs = postOutputModels.stream()
                .map(postDTOModelMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity
                .ok()
                .body(postOutputDTOs);
    }

    @GetMapping("/{email}/liked")
    public ResponseEntity<Set<PostOutputDTO>> getLikedByUser(@NotBlank(message = EMAIL_BLANK_ERROR_MESSAGE)
                                                             @Email(message = EMAIL_NOT_VALID_ERROR_MESSAGE) @PathVariable("email") String email) {
        Set<PostOutputModel> serviceResponse = postService.getLikedByUser(email);

        Set<PostOutputDTO> likedPosts = serviceResponse.stream()
                .map(postDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(likedPosts);
    }

    @PostMapping()
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<Long> create(@Valid @RequestBody PostInputDTO postInputDTO) {
        PostInputModel postInputModel = postDTOModelMapper.toModel(postInputDTO);
        Long serviceResponse = postService.create(postInputModel);
        return ResponseEntity
                .ok()
                .body(serviceResponse);
    }

    @PostMapping("/like")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ResponseEntity<PostOutputDTO> like(@Valid @RequestBody PostLikeDTO postLikeDTO) {
        PostLikesRelationModel postLikesRelationModel = postLikesDTOModelMapper.toModel(postLikeDTO);
        PostOutputModel postOutputModel = postService.likePost(postLikesRelationModel);
        PostOutputDTO postOutputDTO = postDTOModelMapper.toDTO(postOutputModel);
        return ResponseEntity
                .ok()
                .body(postOutputDTO);
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(@Valid @RequestBody PostInputDTO postInputDTO) {
        PostInputModel postInputModel = postDTOModelMapper.toModel(postInputDTO);
        OperationStatus serviceResponse = postService.update(postInputModel);
        return ResponseEntity
                .ok()
                .body(serviceResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationStatus> delete(
            @PathVariable("id")
            @Positive(message = IDENTIFIER_ERROR_MESSAGE)
            @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE)
            Long id
    ) {
        OperationStatus serviceResponse = postService.delete(id);
        return ResponseEntity
                .status(operationHttpStatusMapper.toHttpStatus(serviceResponse))
                .build();
    }
}


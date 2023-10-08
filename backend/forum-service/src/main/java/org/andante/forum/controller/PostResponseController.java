package org.andante.forum.controller;

import dto.response.PostResponseInputDTO;
import dto.response.PostResponseLikeDTO;
import dto.response.PostResponseOutputDTO;
import dto.response.PostResponseQuerySpecification;
import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.forum.controller.mapper.PostResponseDTOModelMapper;
import org.andante.forum.controller.mapper.PostResponsesLikesDTOModelMapper;
import org.andante.forum.logic.model.response.PostResponseInputModel;
import org.andante.forum.logic.model.response.PostResponseOutputModel;
import org.andante.forum.logic.model.response.PostResponsesLikesRelationModel;
import org.andante.forum.logic.service.impl.PostResponseServiceImpl;
import org.andante.mappers.OperationHttpStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/forum/response", produces = MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
public class PostResponseController {

    private static final String IDENTIFIER_ERROR_MESSAGE = "Product identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Product identifier must not be a null";

    private final PostResponseServiceImpl postResponseService;
    private final PostResponseDTOModelMapper postResponseDTOModelMapper;
    private final PostResponsesLikesDTOModelMapper postResponsesLikesDTOModelMapper;
    private final OperationHttpStatusMapper operationHttpStatusMapper;

    @GetMapping
    public ResponseEntity<PostResponseOutputDTO> get(
            @RequestParam("id")
            @Positive(message = IDENTIFIER_ERROR_MESSAGE)
            @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE)
            Long id
    ) {
        PostResponseOutputModel postResponseOutputModel = postResponseService.get(id);
        PostResponseOutputDTO postResponseOutputDTO = postResponseDTOModelMapper.toDTO(postResponseOutputModel);
        return ResponseEntity
                .ok()
                .body(postResponseOutputDTO);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<PostResponseOutputDTO>> getPage(
            @Valid
            PostResponseQuerySpecification postResponseQuerySpecification
    ) {
        Page<PostResponseOutputModel> postResponseOutputModels = postResponseService.getByQuery(postResponseQuerySpecification);
        Page<PostResponseOutputDTO> postResponseOutputDTOs = postResponseOutputModels.map(postResponseDTOModelMapper::toDTO);
        return ResponseEntity.ok().body(postResponseOutputDTOs);
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody PostResponseInputDTO postResponseInputDTO) {
        PostResponseInputModel postResponseInputModel = postResponseDTOModelMapper.toModel(postResponseInputDTO);
        Long serviceResponse = postResponseService.create(postResponseInputModel);
        return ResponseEntity
                .ok()
                .body(serviceResponse);
    }

    @PostMapping("/like")
    public ResponseEntity<PostResponseOutputDTO> like(
            @Valid
            @RequestBody
            PostResponseLikeDTO postResponseLikeDTO
    ) {
        PostResponsesLikesRelationModel postResponsesLikesRelationModel =
                postResponsesLikesDTOModelMapper.toModel(postResponseLikeDTO);
        PostResponseOutputModel postResponseOutputModel =
                postResponseService.likeResponse(postResponsesLikesRelationModel);
        PostResponseOutputDTO postResponseOutputDTO = postResponseDTOModelMapper.toDTO(postResponseOutputModel);
        return ResponseEntity
                .ok()
                .body(postResponseOutputDTO);
    }

    @PutMapping
    public ResponseEntity<OperationStatus> modify(
            @Valid
            @RequestBody
            PostResponseInputDTO postResponseInputDTO
    ) {
        PostResponseInputModel postResponseInputModel = postResponseDTOModelMapper.toModel(postResponseInputDTO);
        OperationStatus serviceResponse = postResponseService.update(postResponseInputModel);
        return ResponseEntity
                .status(operationHttpStatusMapper.toHttpStatus(serviceResponse))
                .build();
    }

    @DeleteMapping
    public ResponseEntity<OperationStatus> delete(
            @RequestParam("id")
            @Positive(message = IDENTIFIER_ERROR_MESSAGE)
            @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE)
            Long id
    ) {
        OperationStatus serviceResponse = postResponseService.delete(id);
        return ResponseEntity
                .status(operationHttpStatusMapper.toHttpStatus(serviceResponse))
                .build();
    }
}

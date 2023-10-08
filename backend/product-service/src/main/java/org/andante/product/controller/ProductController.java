package org.andante.product.controller;

import lombok.RequiredArgsConstructor;
import org.andante.enums.OperationStatus;
import org.andante.mappers.OperationHttpStatusMapper;
import org.andante.product.dto.ProductOutputDTO;
import org.andante.product.dto.ProductQuerySpecification;
import org.andante.product.logic.model.ProductOutput;
import org.andante.product.logic.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/product")
@Validated
public class ProductController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "List of provided identifiers must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Product identifier '${validatedValue}' must be a positive number";
    private static final String USER_EMAIL_ERROR_MESSAGE = "Provided value '${validatedValue}' must be a valid email address";
    private static final String USER_EMAIL_NULL_ERROR_MESSAGE = "Provided email address must not be a null";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Product identifier must not be a null";
    private static final String PAGE_NUMBER_NEGATIVE_ERROR_MESSAGE = "Page number '${validatedValue}' must not be a negative number";
    private static final String PAGE_NUMBER_NULL_ERROR_MESSAGE = "Page number must not be null";
    private static final String PAGE_SIZE_NON_POSITIVE_ERROR_MESSAGE = "Page size '${validatedValue}' must be a positive number";
    private static final String PAGE_SIZE_NULL_ERROR_MESSAGE = "Page size must not be a null";
    private static final String RATING_NULL_ERROR_MESSAGE = "Rating must not be null";
    private static final String RATING_NEGATIVE_ERROR_MESSAGE = "Rating must not be negative";
    private static final String USERNAME_BLANK_ERROR_MESSAGE = "Username must not be blank";

    private final ProductService productService;
    private final OperationHttpStatusMapper operationHttpStatusMapper;

    @GetMapping("/bulk")
    public ResponseEntity<Set<ProductOutputDTO>> getProducts(@RequestParam(name = "ids") @Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE)
                                                             @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set< @Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                             @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<ProductOutput> serviceResponse = productService.getProducts(identifiers);

        Set<ProductOutputDTO> productOutputDTOS = serviceResponse.stream()
                .map(ProductOutput::toDTO)
                .collect(Collectors.toSet());

        return new ResponseEntity<>(productOutputDTOS, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<Set<ProductOutputDTO>> getObserved(@RequestParam(name = "username")
                                                             @NotBlank(message = USERNAME_BLANK_ERROR_MESSAGE) String username) {
        Set<ProductOutput> serviceResponse = productService.getObservedProducts(username);
        Set<ProductOutputDTO> observedProducts = serviceResponse.stream()
                .map(ProductOutput::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(observedProducts);
    }

    @GetMapping("/query")
    public ResponseEntity<Page<ProductOutputDTO>>  getByQuery(@Valid ProductQuerySpecification productQuerySpecification,
                                                              @RequestParam("rating") @NotNull(message = RATING_NULL_ERROR_MESSAGE)
                                                              @PositiveOrZero(message = RATING_NEGATIVE_ERROR_MESSAGE) Double minimumRating) {
        Page<ProductOutput> serviceResponse = productService.getByQuery(productQuerySpecification, minimumRating);
        Page<ProductOutputDTO> products = serviceResponse.map(ProductOutput::toDTO);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/rating")
    public ResponseEntity<List<ProductOutputDTO>> getBestRatedProducts(@PositiveOrZero(message = PAGE_NUMBER_NEGATIVE_ERROR_MESSAGE)
                                                                       @NotNull(message = PAGE_NUMBER_NULL_ERROR_MESSAGE) @RequestParam(name = "page") Integer page,
                                                                       @Positive(message = PAGE_SIZE_NON_POSITIVE_ERROR_MESSAGE)
                                                                       @NotNull(message = PAGE_SIZE_NULL_ERROR_MESSAGE) @RequestParam(name = "size") Integer pageSize) {
        List<ProductOutput> serviceResponse = productService.getByAverageRatingDescending(page, pageSize);
        List<ProductOutputDTO> topProducts = serviceResponse.stream()
                .map(ProductOutput::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(topProducts);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ProductOutputDTO>> getMostPopular(@PositiveOrZero(message = PAGE_NUMBER_NEGATIVE_ERROR_MESSAGE)
                                                                 @NotNull(message = PAGE_NUMBER_NULL_ERROR_MESSAGE) @RequestParam(name = "page") Integer page,
                                                                 @Positive(message = PAGE_SIZE_NON_POSITIVE_ERROR_MESSAGE)
                                                                 @NotNull(message = PAGE_SIZE_NULL_ERROR_MESSAGE) @RequestParam(name = "size") Integer size) {
        List<ProductOutput> serviceResponse = productService.getByObserversCountDescending(page, size);
        List<ProductOutputDTO> mostObservedProducts = serviceResponse.stream()
                .map(ProductOutput::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(mostObservedProducts);
    }

    @PostMapping("/status")
    public ResponseEntity<OperationStatus> changeObservationStatus(@RequestParam(name = "id")  @Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                                   @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier,
                                                                   @RequestParam(name = "user") @Email(message = USER_EMAIL_ERROR_MESSAGE)
                                                                   @NotNull(message = USER_EMAIL_NULL_ERROR_MESSAGE) String emailAddress) {
        OperationStatus serviceResponse = productService.changeObservationStatus(emailAddress, identifier);

        return ResponseEntity.status(operationHttpStatusMapper.toHttpStatus(serviceResponse))
                .build();
    }
}

package org.andante.product.controller;

import lombok.RequiredArgsConstructor;
import org.andante.product.controller.mapper.ProductOrderVariantDTOModelMapper;
import org.andante.product.controller.mapper.ProductOrderViolationDTOModelMapper;
import org.andante.product.dto.ProductOrderVariantDTO;
import org.andante.product.dto.ProductOrderViolationDTO;
import org.andante.product.dto.ProductVariantOutputDTO;
import org.andante.product.logic.model.ProductOrderVariant;
import org.andante.product.logic.model.ProductOrderViolation;
import org.andante.product.logic.model.ProductVariantOutput;
import org.andante.product.logic.service.ProductVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/product/variant")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
public class ProductVariantController {

    private static final String IDENTIFIERS_LIST_ERROR_MESSAGE = "Provided variant identifiers list must contain at least {min} element(s)";
    private static final String IDENTIFIERS_LIST_NULL_ERROR_MESSAGE = "List of provided identifiers must not be a null";
    private static final String IDENTIFIER_ERROR_MESSAGE = "Variant identifier '${validatedValue}' must be a positive number";
    private static final String IDENTIFIER_NULL_ERROR_MESSAGE = "Variant identifier must not be a null";
    private static final String PRODUCT_VARIANT_ORDER_EMPTY_MESSAGE = "List of variants to validate must not be empty";

    private final ProductVariantService productVariantService;
    private final ProductOrderVariantDTOModelMapper productOrderVariantDTOModelMapper;
    private final ProductOrderViolationDTOModelMapper productOrderViolationDTOModelMapper;

    @GetMapping("/ids")
    public ResponseEntity<Set<ProductVariantOutputDTO>> findVariants(@RequestParam(name = "ids") @Size(min = 1, message = IDENTIFIERS_LIST_ERROR_MESSAGE)
                                                                     @NotNull(message = IDENTIFIERS_LIST_NULL_ERROR_MESSAGE) Set<@Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                                     @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long> identifiers) {
        Set<ProductVariantOutput> serviceResponse = productVariantService.getVariants(identifiers);

        Set<ProductVariantOutputDTO> productVariants = serviceResponse.stream()
                .map(ProductVariantOutput::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .body(productVariants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantOutputDTO> findVariant(@PathVariable("id") @Positive(message = IDENTIFIER_ERROR_MESSAGE)
                                                               @NotNull(message = IDENTIFIER_NULL_ERROR_MESSAGE) Long identifier) {
        Optional<ProductVariantOutputDTO> serviceResponse = productVariantService.getVariant(identifier).map(ProductVariantOutput::toDTO);

        if (serviceResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok()
                .body(serviceResponse.get());
    }

    @PostMapping("/validate/order")
    public ResponseEntity<Set<ProductOrderViolationDTO>> validateOrder(@RequestBody @NotEmpty(message = PRODUCT_VARIANT_ORDER_EMPTY_MESSAGE) Set<@Valid ProductOrderVariantDTO> productOrderVariants) {
        Set<ProductOrderVariant> mappedProductOrders = productOrderVariants.stream()
                .map(productOrderVariantDTOModelMapper::toModel)
                .collect(Collectors.toSet());

        Set<ProductOrderViolation> serviceResponse = productVariantService.validateOrder(mappedProductOrders);
        Set<ProductOrderViolationDTO> productOrderViolations = serviceResponse.stream()
                .map(productOrderViolationDTOModelMapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(productOrderViolations);
    }
}

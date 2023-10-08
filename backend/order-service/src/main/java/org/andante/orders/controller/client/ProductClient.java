package org.andante.orders.controller.client;

import org.andante.product.dto.ProductOrderVariantDTO;
import org.andante.product.dto.ProductOrderViolationDTO;
import org.andante.product.dto.ProductVariantOutputDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@FeignClient(name="PRODUCT/product")
public interface ProductClient {
    @GetMapping("/variant/ids")
    ResponseEntity<Set<ProductVariantOutputDTO>> getVariantsByIds(@RequestParam(name="ids") Set<Long> identifiers);

    @PostMapping("/variant/validate/order")
    ResponseEntity<Set<ProductOrderViolationDTO>> validateOrder(@RequestBody Set<ProductOrderVariantDTO> productOrderVariants);
}

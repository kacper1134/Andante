package org.andante.product.logic.service;

import org.andante.enums.OperationStatus;
import org.andante.product.dto.ProductQuerySpecification;
import org.andante.product.logic.model.ProductOutput;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface ProductService {
    Set<ProductOutput> getProducts(Set<Long> identifiers);
    Set<ProductOutput> getObservedProducts(String username);
    Page<ProductOutput> getByQuery(ProductQuerySpecification querySpecification, Double minimumRating);
    List<ProductOutput> getByObserversCountDescending(Integer page, Integer count);
    OperationStatus changeObservationStatus(String observer, Long identifier);
    List<ProductOutput> getByAverageRatingDescending(Integer page, Integer pageSize);
}

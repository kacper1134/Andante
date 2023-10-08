package org.andante.product.logic.specification;

import org.andante.product.dto.ProductQuerySpecification;
import org.andante.product.enums.ProductSortingOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class ProductSpecificationBuilder {

    public Pageable getPageSpecification(ProductQuerySpecification productQuerySpecification) {
        return PageRequest.of(productQuerySpecification.getPageNumber(), productQuerySpecification.getPageSize(),
                getSortingMethod(productQuerySpecification.getSortingOrder()));
    }

    private Sort getSortingMethod(ProductSortingOrder productSortingOrder) {
        switch (productSortingOrder) {
            case RECENTLY_ADDED:
                return Sort.by("creationTimestamp").descending();
            case PRICE_ASCENDING:
                return Sort.by("basePrice");
            case PRICE_DESCENDING:
                return Sort.by("basePrice").descending();
            default:
                return Sort.by("name");
        }
    }
}

package org.andante.gramophones.logic.service;

import org.andante.gramophones.logic.model.GramophonesInput;
import org.andante.gramophones.logic.model.GramophonesOutput;
import org.andante.product.dto.ProductQuerySpecification;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface GramophonesService {

    Set<GramophonesOutput> getAllById(Set<Long> identifiers);
    Page<GramophonesOutput> getByQuery(ProductQuerySpecification querySpecification, Double minimumRating);
    GramophonesOutput create(GramophonesInput gramophonesInput);
    GramophonesOutput modify(GramophonesInput gramophonesInput);
    GramophonesOutput delete(Long identifier);
}

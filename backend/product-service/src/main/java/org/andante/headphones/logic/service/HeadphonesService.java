package org.andante.headphones.logic.service;

import org.andante.headphones.logic.model.HeadphonesInput;
import org.andante.headphones.logic.model.HeadphonesOutput;
import org.andante.product.dto.ProductQuerySpecification;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface HeadphonesService {
    Set<HeadphonesOutput> findAllById(Set<Long> identifiers);
    Page<HeadphonesOutput> getByQuery(ProductQuerySpecification querySpecification, Double minimumRating);
    HeadphonesOutput create(HeadphonesInput headphonesInput);
    HeadphonesOutput modify(HeadphonesInput headphonesInput);
    HeadphonesOutput delete(Long identifier);
}

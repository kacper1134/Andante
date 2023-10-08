package org.andante.microphones.logic.service;

import org.andante.microphones.logic.model.MicrophonesInput;
import org.andante.microphones.logic.model.MicrophonesOutput;
import org.andante.product.dto.ProductQuerySpecification;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface MicrophonesService {
    Set<MicrophonesOutput> getAllByIds(Set<Long> identifiers);
    Page<MicrophonesOutput> getByQuery(ProductQuerySpecification querySpecification, Double minimumRating);
    MicrophonesOutput create(MicrophonesInput microphonesInput);
    MicrophonesOutput modify(MicrophonesInput microphonesInput);
    MicrophonesOutput delete(Long identifier);
}

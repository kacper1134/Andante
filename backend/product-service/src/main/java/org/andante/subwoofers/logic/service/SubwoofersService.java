package org.andante.subwoofers.logic.service;

import org.andante.product.dto.ProductQuerySpecification;
import org.andante.subwoofers.logic.model.SubwoofersInput;
import org.andante.subwoofers.logic.model.SubwoofersOutput;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface SubwoofersService {
    Set<SubwoofersOutput> getAllById(Set<Long> identifiers);
    Page<SubwoofersOutput> getByQuery(ProductQuerySpecification querySpecification, Double minimumRating);

    SubwoofersOutput create(SubwoofersInput speakersInput);
    SubwoofersOutput modify(SubwoofersInput speakersInput);
    SubwoofersOutput delete(Long identifier);
}

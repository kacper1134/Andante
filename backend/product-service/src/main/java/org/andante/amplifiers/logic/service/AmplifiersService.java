package org.andante.amplifiers.logic.service;

import org.andante.amplifiers.logic.model.AmplifiersInput;
import org.andante.amplifiers.logic.model.AmplifiersOutput;
import org.andante.product.dto.ProductQuerySpecification;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface AmplifiersService {
    Set<AmplifiersOutput> getAllById(Set<Long> identifiers);
    Page<AmplifiersOutput> getByQuery(ProductQuerySpecification specification, Double minimumRating);
    AmplifiersOutput create(AmplifiersInput amplifiersInput);
    AmplifiersOutput update(AmplifiersInput amplifiersInput);
    AmplifiersOutput delete(Long identifier);
}

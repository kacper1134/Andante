package org.andante.speakers.logic.service;

import org.andante.product.dto.ProductQuerySpecification;
import org.andante.speakers.logic.model.SpeakersInput;
import org.andante.speakers.logic.model.SpeakersOutput;
import org.springframework.data.domain.Page;

import java.util.Set;

public interface SpeakersService {
    Set<SpeakersOutput> getAllById(Set<Long> identifiers);
    Page<SpeakersOutput> getByQuery(ProductQuerySpecification querySpecification, Double minimumRating);
    SpeakersOutput create(SpeakersInput speakersInput);
    SpeakersOutput modify(SpeakersInput speakersInput);
    SpeakersOutput delete(Long identifier);
}

package org.andante.speakers.logic.service;

import org.andante.speakers.logic.model.SpeakersVariantInput;
import org.andante.speakers.logic.model.SpeakersVariantOutput;

import java.util.Set;

public interface SpeakersVariantService {
    Set<SpeakersVariantOutput> getAllById(Set<Long> identifiers);
    Set<SpeakersVariantOutput> getAllBySpeakersId(Long speakersId);
    SpeakersVariantOutput create(SpeakersVariantInput speakersVariant);
    SpeakersVariantOutput modify(SpeakersVariantInput speakersVariant);
    SpeakersVariantOutput delete(Long identifier);
}

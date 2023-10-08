package org.andante.amplifiers.logic.service;

import org.andante.amplifiers.logic.model.AmplifiersVariantInput;
import org.andante.amplifiers.logic.model.AmplifiersVariantOutput;

import java.util.Set;

public interface AmplifiersVariantService {
    Set<AmplifiersVariantOutput> getAllById(Set<Long> identifiers);
    Set<AmplifiersVariantOutput> getAllByProductId(Long productIdentifier);
    AmplifiersVariantOutput create(AmplifiersVariantInput amplifiersVariant);
    AmplifiersVariantOutput update(AmplifiersVariantInput amplifiersVariant);
    AmplifiersVariantOutput delete(Long identifier);
}

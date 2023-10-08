package org.andante.gramophones.logic.service;

import org.andante.gramophones.logic.model.GramophonesVariantInput;
import org.andante.gramophones.logic.model.GramophonesVariantOutput;

import java.util.Set;

public interface GramophonesVariantService {
    Set<GramophonesVariantOutput> getAllByIds(Set<Long> identifiers);
    Set<GramophonesVariantOutput> getAllByGramophoneId(Long gramophoneId);
    GramophonesVariantOutput create(GramophonesVariantInput gramophonesVariant);
    GramophonesVariantOutput modify(GramophonesVariantInput gramophonesVariant);
    GramophonesVariantOutput delete(Long identifier);
}

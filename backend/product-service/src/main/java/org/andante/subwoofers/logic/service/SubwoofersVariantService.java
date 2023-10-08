package org.andante.subwoofers.logic.service;

import org.andante.subwoofers.logic.model.SubwoofersVariantInput;
import org.andante.subwoofers.logic.model.SubwoofersVariantOutput;

import java.util.Set;

public interface SubwoofersVariantService {
    Set<SubwoofersVariantOutput> getAllById(Set<Long> identifiers);
    Set<SubwoofersVariantOutput> getAllBySubwooferId(Long subwooferId);
    SubwoofersVariantOutput create(SubwoofersVariantInput subwoofersVariant);
    SubwoofersVariantOutput modify(SubwoofersVariantInput subwoofersVariant);
    SubwoofersVariantOutput delete(Long identifier);
}

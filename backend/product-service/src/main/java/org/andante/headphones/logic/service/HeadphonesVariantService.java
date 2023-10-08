package org.andante.headphones.logic.service;

import org.andante.headphones.logic.model.HeadphonesVariantInput;
import org.andante.headphones.logic.model.HeadphonesVariantOutput;

import java.util.Set;

public interface HeadphonesVariantService {
    Set<HeadphonesVariantOutput> getAllById(Set<Long> identifiers);
    Set<HeadphonesVariantOutput> getAllByHeadphonesId(Long headphonesId);
    HeadphonesVariantOutput create(HeadphonesVariantInput headphonesVariant);
    HeadphonesVariantOutput modify(HeadphonesVariantInput headphonesVariant);
    HeadphonesVariantOutput delete(Long identifier);
}

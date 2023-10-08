package org.andante.microphones.logic.service;

import org.andante.microphones.logic.model.MicrophonesVariantInput;
import org.andante.microphones.logic.model.MicrophonesVariantOutput;

import java.util.Set;

public interface MicrophonesVariantService {
    Set<MicrophonesVariantOutput> getAllById(Set<Long> identifiers);
    Set<MicrophonesVariantOutput> getAllByMicrophoneId(Long microphoneId);
    MicrophonesVariantOutput create(MicrophonesVariantInput microphonesVariant);
    MicrophonesVariantOutput modify(MicrophonesVariantInput microphonesVariant);
    MicrophonesVariantOutput delete(Long identifier);
}

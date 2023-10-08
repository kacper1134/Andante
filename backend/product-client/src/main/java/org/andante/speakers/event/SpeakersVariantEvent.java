package org.andante.speakers.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.enums.OperationType;
import org.andante.speakers.dto.SpeakersVariantOutputDTO;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SpeakersVariantEvent {
    private SpeakersVariantOutputDTO speakersVariant;
    private OperationType operationType;
}

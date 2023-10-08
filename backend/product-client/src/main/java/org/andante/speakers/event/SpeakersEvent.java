package org.andante.speakers.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.enums.OperationType;
import org.andante.speakers.dto.SpeakersOutputDTO;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SpeakersEvent {
    private SpeakersOutputDTO speakers;
    private OperationType operationType;
}

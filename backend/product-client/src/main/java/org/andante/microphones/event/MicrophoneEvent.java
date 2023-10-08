package org.andante.microphones.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.enums.OperationType;
import org.andante.microphones.dto.MicrophonesOutputDTO;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MicrophoneEvent {
    private MicrophonesOutputDTO microphone;
    private OperationType operationType;
}

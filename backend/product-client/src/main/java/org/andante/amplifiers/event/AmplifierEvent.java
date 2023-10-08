package org.andante.amplifiers.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.amplifiers.dto.AmplifiersOutputDTO;
import org.andante.enums.OperationType;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AmplifierEvent {
    private AmplifiersOutputDTO amplifiers;
    private OperationType operationType;
}

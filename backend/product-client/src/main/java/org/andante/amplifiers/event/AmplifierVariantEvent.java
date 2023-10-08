package org.andante.amplifiers.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.amplifiers.dto.AmplifiersVariantOutputDTO;
import org.andante.enums.OperationType;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AmplifierVariantEvent {

    private AmplifiersVariantOutputDTO amplifierVariant;
    private OperationType operationType;
}

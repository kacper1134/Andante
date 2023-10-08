package org.andante.gramophones.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.enums.OperationType;
import org.andante.gramophones.dto.GramophonesOutputDTO;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GramophoneEvent {
    private GramophonesOutputDTO gramophone;
    private OperationType operationType;
}

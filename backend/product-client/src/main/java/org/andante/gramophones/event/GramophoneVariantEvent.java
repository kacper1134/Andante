package org.andante.gramophones.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.enums.OperationType;
import org.andante.gramophones.dto.GramophonesVariantOutputDTO;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GramophoneVariantEvent {
    private GramophonesVariantOutputDTO gramophoneVariant;
    private OperationType operationType;
}

package org.andante.subwoofers.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.enums.OperationType;
import org.andante.subwoofers.dto.SubwoofersVariantOutputDTO;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubwoofersVariantEvent {
    private SubwoofersVariantOutputDTO subwoofersVariant;
    private OperationType operationType;
}

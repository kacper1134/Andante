package org.andante.subwoofers.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.enums.OperationType;
import org.andante.subwoofers.dto.SubwoofersOutputDTO;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubwoofersEvent {
    private SubwoofersOutputDTO subwoofers;
    private OperationType operationType;
}

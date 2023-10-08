package org.andante.headphones.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.enums.OperationType;
import org.andante.headphones.dto.HeadphonesOutputDTO;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HeadphonesEvent {
    private HeadphonesOutputDTO headphones;
    private OperationType operationType;
}

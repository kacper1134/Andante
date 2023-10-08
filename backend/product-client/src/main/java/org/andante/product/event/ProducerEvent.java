package org.andante.product.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.enums.OperationType;
import org.andante.product.dto.ProducerDTO;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProducerEvent {
    private ProducerDTO producer;
    private OperationType operationType;
}

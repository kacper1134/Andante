package org.andante.headphones.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.enums.OperationType;
import org.andante.headphones.dto.HeadphonesVariantOutputDTO;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HeadphonesVariantEvent {
    private HeadphonesVariantOutputDTO headphonesVariant;
    private OperationType operationType;
}

package org.andante.subwoofers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.product.dto.ProductVariantOutputDTO;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubwoofersVariantOutputDTO extends ProductVariantOutputDTO {

    private String color;
    private Long subwoofersId;
}

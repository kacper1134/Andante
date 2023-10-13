package org.andante.subwoofers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.product.dto.ProductOutputDTO;
import org.andante.subwoofers.enums.SubwooferType;

import java.util.List;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubwoofersOutputDTO extends ProductOutputDTO {

    private Float power;
    private SubwooferType type;
    private List<SubwoofersVariantOutputDTO> variants;
}

package org.andante.product.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.amplifiers.dto.AmplifiersVariantOutputDTO;
import org.andante.gramophones.dto.GramophonesVariantOutputDTO;
import org.andante.headphones.dto.HeadphonesVariantOutputDTO;
import org.andante.microphones.dto.MicrophonesVariantOutputDTO;
import org.andante.speakers.dto.SpeakersVariantOutputDTO;
import org.andante.subwoofers.dto.SubwoofersVariantOutputDTO;

import java.math.BigDecimal;
import java.util.Set;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(AmplifiersVariantOutputDTO.class),
        @JsonSubTypes.Type(GramophonesVariantOutputDTO.class),
        @JsonSubTypes.Type(HeadphonesVariantOutputDTO.class),
        @JsonSubTypes.Type(MicrophonesVariantOutputDTO.class),
        @JsonSubTypes.Type(SpeakersVariantOutputDTO.class),
        @JsonSubTypes.Type(SubwoofersVariantOutputDTO.class)
})
public abstract class ProductVariantOutputDTO {

    private Long id;
    private BigDecimal price;
    private Integer availableQuantity;
    private String imageUrl;
    private String thumbnailUrl;
    private Set<String> observers;
    private String productName;
}

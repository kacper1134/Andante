package org.andante.product.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.andante.amplifiers.dto.AmplifiersOutputDTO;
import org.andante.gramophones.dto.GramophonesOutputDTO;
import org.andante.headphones.dto.HeadphonesOutputDTO;
import org.andante.microphones.dto.MicrophonesOutputDTO;
import org.andante.product.enums.ProductType;
import org.andante.speakers.dto.SpeakersOutputDTO;
import org.andante.subwoofers.dto.SubwoofersOutputDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(AmplifiersOutputDTO.class),
    @JsonSubTypes.Type(GramophonesOutputDTO.class),
    @JsonSubTypes.Type(HeadphonesOutputDTO.class),
    @JsonSubTypes.Type(MicrophonesOutputDTO.class),
    @JsonSubTypes.Type(SpeakersOutputDTO.class),
    @JsonSubTypes.Type(SubwoofersOutputDTO.class)
})
public abstract class ProductOutputDTO {

    private Long id;
    private String name;
    private String description;
    private Float weight;
    private BigDecimal price;
    private Long minimumFrequency;
    private Long maximumFrequency;
    private ProductType productType;
    @EqualsAndHashCode.Exclude private LocalDateTime creationTimestamp;
    @EqualsAndHashCode.Exclude private LocalDateTime modificationTimestamp;
    private List<CommentDTO> comments;
    private Double averageRating;
    private Set<String> observers;
    private ProducerDTO producer;
}

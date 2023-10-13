package org.andante.amplifiers.utils;

import org.andante.amplifiers.configuration.AmplifiersTestConfiguration;
import org.andante.amplifiers.dto.AmplifiersInputDTO;
import org.andante.amplifiers.dto.AmplifiersVariantInputDTO;
import org.andante.amplifiers.enums.AmplifierType;
import org.andante.amplifiers.logic.model.AmplifiersInput;
import org.andante.amplifiers.logic.model.AmplifiersVariantInput;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.repository.entity.AmplifiersVariantEntity;
import org.andante.product.dto.CommentDTO;
import org.andante.product.dto.ProducerDTO;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.product.repository.entity.ProducerEntity;
import org.andante.product.repository.entity.ProductVariantEntity;
import org.jeasy.random.EasyRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Import(AmplifiersTestConfiguration.class)
public class AmplifiersTestUtils {

    private final EasyRandom generator;

    @Autowired
    public AmplifiersTestUtils(@Qualifier("Amplifiers") EasyRandom generator) {
        this.generator = generator;
    }

    public Set<AmplifiersEntity> generateAmplifiers(int size) {
        Set<AmplifiersEntity> amplifiers = generator.objects(AmplifiersEntity.class, size)
                .collect(Collectors.toSet());

        amplifiers.forEach(amplifier -> amplifier.getComments()
                .forEach(comment -> comment.setProduct(amplifier)));
        amplifiers.forEach(amplifier -> amplifier.setVariants(Set.of()));

        return amplifiers;
    }

    public AmplifiersEntity generateAmplifier() {
        AmplifiersEntity amplifiers = generator.nextObject(AmplifiersEntity.class);

        amplifiers.getComments()
                .forEach(comment -> comment.setProduct(amplifiers));
        amplifiers.setVariants(Set.of());

        return amplifiers;
    }

    public <T> T generate(Class<T> type) {
        return generator.nextObject(type);
    }

    public <T> Set<T> generate(Class<T> type, int count) {
        return generator.objects(type, count).collect(Collectors.toSet());
    }

    public AmplifiersInput toInput(AmplifiersEntity amplifiers) {
        return AmplifiersInput.builder()
                .id(amplifiers.getId())
                .name(amplifiers.getName())
                .weight(amplifiers.getWeight())
                .basePrice(amplifiers.getBasePrice())
                .productType(amplifiers.getProductType())
                .commentsIds(amplifiers.getComments().stream()
                        .map(CommentEntity::getId)
                        .collect(Collectors.toSet()))
                .observers(amplifiers.getObservers())
                .minimumFrequency(amplifiers.getMinimumFrequency())
                .maximumFrequency(amplifiers.getMaximumFrequency())
                .producerName(Optional.ofNullable(amplifiers.getProducer()).map(ProducerEntity::getName).orElse(""))
                .power(amplifiers.getPower())
                .amplifierType(amplifiers.getType())
                .variantsIds(Optional.ofNullable(amplifiers.getVariants()).orElse(List.of()).stream()
                        .map(ProductVariantEntity::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

    public AmplifiersVariantInput toInput(AmplifiersVariantEntity variant) {
        return AmplifiersVariantInput.builder()
                .id(variant.getId())
                .priceDifference(variant.getPriceDifference())
                .availableQuantity(variant.getAvailableQuantity())
                .imageUrl(variant.getImageUrl())
                .color(variant.getColor())
                .amplifiersId(variant.getAmplifiers().getId())
                .build();
    }

    public AmplifiersInputDTO toInputDTO(AmplifiersEntity amplifiersEntity) {
        return AmplifiersInputDTO.builder()
                .id(amplifiersEntity.getId())
                .name(amplifiersEntity.getName())
                .description(amplifiersEntity.getDescription())
                .weight(amplifiersEntity.getWeight())
                .price(amplifiersEntity.getBasePrice())
                .commentIds(amplifiersEntity.getComments().stream()
                        .map(CommentEntity::getId)
                        .collect(Collectors.toSet()))
                .observers(amplifiersEntity.getObservers())
                .minimumFrequency(amplifiersEntity.getMinimumFrequency())
                .maximumFrequency(amplifiersEntity.getMaximumFrequency())
                .producerName(Optional.ofNullable(amplifiersEntity.getProducer()).map(ProducerEntity::getName).orElse(""))
                .power(amplifiersEntity.getPower())
                .amplifierType(amplifiersEntity.getType())
                .variantsIds(amplifiersEntity.getVariants().stream()
                        .map(ProductVariantEntity::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

    public AmplifiersVariantInputDTO toInputDTO(AmplifiersVariantEntity variant) {
        return AmplifiersVariantInputDTO.builder()
                .id(variant.getId())
                .priceDifference(variant.getPriceDifference())
                .availableQuantity(variant.getAvailableQuantity())
                .imageUrl(variant.getImageUrl())
                .color(variant.getColor())
                .amplifiersId(variant.getAmplifiers().getId())
                .build();
    }

    public AmplifiersInputDTO setValidData(AmplifiersInputDTO amplifiersInputDTO) {
        amplifiersInputDTO.setWeight(50.0F);
        amplifiersInputDTO.setPrice(BigDecimal.ONE);
        amplifiersInputDTO.setMinimumFrequency(5L);
        amplifiersInputDTO.setMaximumFrequency(10L);
        amplifiersInputDTO.setCommentIds(Set.of());
        amplifiersInputDTO.setPower(50.0F);
        amplifiersInputDTO.setObservers(Set.of());
        amplifiersInputDTO.setAmplifierType(AmplifierType.OPERATIONAL);
        amplifiersInputDTO.setVariantsIds(Set.of());

        return amplifiersInputDTO;
    }

    public AmplifiersVariantInputDTO buildValidVariant() {
        return AmplifiersVariantInputDTO.builder()
                .id(1L)
                .priceDifference(BigDecimal.ONE)
                .availableQuantity(10)
                .imageUrl("https://www.pwr.pl")
                .thumbnailUrl("https://www.pwr.pl")
                .color("Yellow")
                .amplifiersId(Math.abs(generate(Long.class)))
                .build();

    }

    public CommentDTO buildValidComment(Long productId) {
        return CommentDTO.builder()
                .id(1L)
                .username("test@gmail.com")
                .rating(2.5f)
                .title("Some title")
                .content("Some content")
                .productId(productId)
                .observers(Set.of())
                .build();
    }

    public ProducerDTO buildValidProducer() {
        return ProducerDTO.builder()
                .name("SampleName")
                .websiteUrl("https://sample.com")
                .imageUrl("https://sample.com")
                .productsIds(Set.of())
                .build();
    }
}

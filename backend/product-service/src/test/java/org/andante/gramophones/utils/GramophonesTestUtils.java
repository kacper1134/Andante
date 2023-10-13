package org.andante.gramophones.utils;

import org.andante.gramophones.configuration.GramophonesTestConfiguration;
import org.andante.gramophones.dto.GramophonesInputDTO;
import org.andante.gramophones.dto.GramophonesVariantInputDTO;
import org.andante.gramophones.enums.ConnectivityTechnology;
import org.andante.gramophones.enums.MotorType;
import org.andante.gramophones.enums.PowerSource;
import org.andante.gramophones.enums.TurntableMaterial;
import org.andante.gramophones.logic.model.GramophonesInput;
import org.andante.gramophones.logic.model.GramophonesVariantInput;
import org.andante.gramophones.repository.entity.GramophonesEntity;
import org.andante.gramophones.repository.entity.GramophonesVariantEntity;
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
@Import(GramophonesTestConfiguration.class)
public class GramophonesTestUtils {

    private final EasyRandom generator;

    @Autowired
    public GramophonesTestUtils(@Qualifier("Gramophones") EasyRandom generator) {
        this.generator = generator;
    }

    public Set<GramophonesEntity> generateGramophones(int size) {
        Set<GramophonesEntity> gramophones = generator.objects(GramophonesEntity.class, size)
                .collect(Collectors.toSet());

        gramophones.forEach(gramophone -> gramophone.getComments()
                .forEach(comment -> comment.setProduct(gramophone)));

        gramophones.forEach(gramophone -> gramophone.setVariants(Set.of()));

        return gramophones;
    }

    public GramophonesEntity generateGramophone() {
        GramophonesEntity gramophones = generator.nextObject(GramophonesEntity.class);

        gramophones.getComments()
                .forEach(comment -> comment.setProduct(gramophones));
        gramophones.setVariants(Set.of());

        return gramophones;
    }

    public <T> T generate(Class<T> type) {
        return generator.nextObject(type);
    }

    public <T> Set<T> generate(Class<T> type, int count) {
        return generator.objects(type, count).collect(Collectors.toSet());
    }

    public GramophonesInput toInput(GramophonesEntity gramophones) {
        return GramophonesInput.builder()
                .id(gramophones.getId())
                .name(gramophones.getName())
                .weight(gramophones.getWeight())
                .basePrice(gramophones.getBasePrice())
                .productType(gramophones.getProductType())
                .commentsIds(gramophones.getComments().stream()
                        .map(CommentEntity::getId)
                        .collect(Collectors.toSet()))
                .observers(gramophones.getObservers())
                .minimumFrequency(gramophones.getMinimumFrequency())
                .maximumFrequency(gramophones.getMaximumFrequency())
                .producerName(Optional.ofNullable(gramophones.getProducer()).map(ProducerEntity::getName).orElse(""))
                .connectivityTechnology(gramophones.getConnectivityTechnology())
                .turntableMaterial(gramophones.getTurntableMaterial())
                .motorType(gramophones.getMotorType())
                .powerSource(gramophones.getPowerSource())
                .maximumRotationalSpeed(gramophones.getMaximumRotationalSpeed())
                .variantsIds(Optional.ofNullable(gramophones.getVariants()).orElse(List.of()).stream()
                        .map(ProductVariantEntity::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

    public GramophonesVariantInput toInput(GramophonesVariantEntity variant) {
        return GramophonesVariantInput.builder()
                .id(variant.getId())
                .priceDifference(variant.getPriceDifference())
                .availableQuantity(variant.getAvailableQuantity())
                .imageUrl(variant.getImageUrl())
                .color(variant.getColor())
                .gramophonesId(variant.getGramophones().getId())
                .build();
    }

    public GramophonesInputDTO toInputDTO(GramophonesEntity gramophones) {
        return GramophonesInputDTO.builder()
                .id(gramophones.getId())
                .name(gramophones.getName())
                .description(gramophones.getDescription())
                .weight(gramophones.getWeight())
                .price(gramophones.getBasePrice())
                .commentIds(gramophones.getComments().stream()
                        .map(CommentEntity::getId)
                        .collect(Collectors.toSet()))
                .observers(gramophones.getObservers())
                .minimumFrequency(gramophones.getMinimumFrequency())
                .maximumFrequency(gramophones.getMaximumFrequency())
                .producerName(Optional.ofNullable(gramophones.getProducer()).map(ProducerEntity::getName).orElse(""))
                .connectivityTechnology(gramophones.getConnectivityTechnology())
                .turntableMaterial(gramophones.getTurntableMaterial())
                .motorType(gramophones.getMotorType())
                .powerSource(gramophones.getPowerSource())
                .maximumRotationalSpeed(gramophones.getMaximumRotationalSpeed())
                .variantsIds(Optional.ofNullable(gramophones.getVariants()).orElse(List.of()).stream()
                        .map(ProductVariantEntity::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

    public GramophonesVariantInputDTO toInputDTO(GramophonesVariantEntity variant) {
        return GramophonesVariantInputDTO.builder()
                .id(variant.getId())
                .priceDifference(variant.getPriceDifference())
                .availableQuantity(variant.getAvailableQuantity())
                .imageUrl(variant.getImageUrl())
                .color(variant.getColor())
                .gramophonesId(variant.getGramophones().getId())
                .build();
    }

    public GramophonesInputDTO setValidData(GramophonesInputDTO gramophonesInputDTO) {
        gramophonesInputDTO.setWeight(50.0F);
        gramophonesInputDTO.setPrice(BigDecimal.ONE);
        gramophonesInputDTO.setMinimumFrequency(5L);
        gramophonesInputDTO.setMaximumFrequency(10L);
        gramophonesInputDTO.setCommentIds(Set.of());
        gramophonesInputDTO.setObservers(Set.of());
        gramophonesInputDTO.setVariantsIds(Set.of());
        gramophonesInputDTO.setConnectivityTechnology(ConnectivityTechnology.USB);
        gramophonesInputDTO.setTurntableMaterial(TurntableMaterial.ACRYLIC);
        gramophonesInputDTO.setMotorType(MotorType.AC);
        gramophonesInputDTO.setPowerSource(PowerSource.POWER_ADAPTER);
        gramophonesInputDTO.setMaximumRotationalSpeed(5);

        return gramophonesInputDTO;
    }

    public GramophonesVariantInputDTO buildValidVariant() {
        return GramophonesVariantInputDTO.builder()
                .id(1L)
                .priceDifference(BigDecimal.ONE)
                .availableQuantity(10)
                .imageUrl("https://www.pwr.pl")
                .thumbnailUrl("https://www.pwr.pl")
                .color("Yellow")
                .gramophonesId(Math.abs(generate(Long.class)))
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
                .productsIds(Set.of())
                .build();
    }
}

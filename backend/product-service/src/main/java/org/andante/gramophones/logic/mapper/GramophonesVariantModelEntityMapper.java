package org.andante.gramophones.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.gramophones.exception.GramophonesNotFoundException;
import org.andante.gramophones.logic.model.GramophonesVariantInput;
import org.andante.gramophones.logic.model.GramophonesVariantOutput;
import org.andante.gramophones.repository.GramophonesRepository;
import org.andante.gramophones.repository.entity.GramophonesEntity;
import org.andante.gramophones.repository.entity.GramophonesVariantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GramophonesVariantModelEntityMapper {

    private static final String GRAMOPHONES_NOT_FOUND_EXCEPTION_MESSAGE = "Gramophones with identifier %d do not exist";

    private final GramophonesRepository gramophonesRepository;
    
    public GramophonesVariantOutput toModel(GramophonesVariantEntity gramophonesVariantEntity) {
        return gramophonesVariantEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public GramophonesVariantEntity toEntity(GramophonesVariantInput gramophonesVariant) {
        GramophonesEntity gramophonesEntity = gramophonesRepository.findById(gramophonesVariant.getGramophonesId())
                .orElseThrow(() -> new GramophonesNotFoundException(String.format(GRAMOPHONES_NOT_FOUND_EXCEPTION_MESSAGE, gramophonesVariant.getGramophonesId())));

        return GramophonesVariantEntity.builder()
                .id(gramophonesVariant.getId())
                .priceDifference(gramophonesVariant.getPriceDifference())
                .availableQuantity(gramophonesVariant.getAvailableQuantity())
                .imageUrl(gramophonesVariant.getImageUrl())
                .thumbnailUrl(gramophonesVariant.getThumbnailUrl())
                .color(gramophonesVariant.getColor())
                .gramophones(gramophonesEntity)
                .build();
    }
}

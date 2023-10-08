package org.andante.headphones.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.headphones.exception.HeadphonesNotFoundException;
import org.andante.headphones.logic.model.HeadphonesVariantInput;
import org.andante.headphones.logic.model.HeadphonesVariantOutput;
import org.andante.headphones.repository.HeadphonesRepository;
import org.andante.headphones.repository.entity.HeadphonesEntity;
import org.andante.headphones.repository.entity.HeadphonesVariantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class HeadphonesVariantModelEntityMapper {

    private static final String HEADPHONES_NOT_FOUND_EXCEPTION_MESSAGE = "Headphones with identifier %d do not exist";

    private final HeadphonesRepository headphonesRepository;

    public HeadphonesVariantOutput toModel(HeadphonesVariantEntity headphonesVariantEntity) {
        return headphonesVariantEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public HeadphonesVariantEntity toEntity(HeadphonesVariantInput headphonesVariant) {
        HeadphonesEntity headphonesEntity = headphonesRepository.findById(headphonesVariant.getHeadphonesId())
                .orElseThrow(() -> new HeadphonesNotFoundException(String.format(HEADPHONES_NOT_FOUND_EXCEPTION_MESSAGE, headphonesVariant.getHeadphonesId())));

        return HeadphonesVariantEntity.builder()
                .id(headphonesVariant.getId())
                .priceDifference(headphonesVariant.getPriceDifference())
                .availableQuantity(headphonesVariant.getAvailableQuantity())
                .imageUrl(headphonesVariant.getImageUrl())
                .thumbnailUrl(headphonesVariant.getThumbnailUrl())
                .nominalImpedance(headphonesVariant.getNominalImpedance())
                .loudness(headphonesVariant.getLoudness())
                .color(headphonesVariant.getColor())
                .headphones(headphonesEntity)
                .build();
    }
}

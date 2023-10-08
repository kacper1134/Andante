package org.andante.amplifiers.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.amplifiers.exception.AmplifiersNotFoundException;
import org.andante.amplifiers.logic.model.AmplifiersVariantInput;
import org.andante.amplifiers.logic.model.AmplifiersVariantOutput;
import org.andante.amplifiers.repository.AmplifiersRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.repository.entity.AmplifiersVariantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AmplifiersVariantModelEntityMapper {

    private static final String AMPLIFIERS_NOT_FOUND_EXCEPTION_MESSAGE = "Amplifiers with id %d do not exist";

    private final AmplifiersRepository amplifiersRepository;

    public AmplifiersVariantOutput toModel(AmplifiersVariantEntity amplifiersVariantEntity) {
        return amplifiersVariantEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public AmplifiersVariantEntity toEntity(AmplifiersVariantInput amplifiersVariant) {
        AmplifiersEntity amplifiers = amplifiersRepository.findById(amplifiersVariant.getAmplifiersId())
                .orElseThrow(() -> new AmplifiersNotFoundException(String.format(AMPLIFIERS_NOT_FOUND_EXCEPTION_MESSAGE, amplifiersVariant.getAmplifiersId())));

        return AmplifiersVariantEntity.builder()
                .id(amplifiersVariant.getId())
                .priceDifference(amplifiersVariant.getPriceDifference())
                .availableQuantity(amplifiersVariant.getAvailableQuantity())
                .imageUrl(amplifiersVariant.getImageUrl())
                .thumbnailUrl(amplifiersVariant.getThumbnailUrl())
                .color(amplifiersVariant.getColor())
                .amplifiers(amplifiers)
                .build();
    }
}

package org.andante.subwoofers.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.subwoofers.exception.SubwoofersNotFoundException;
import org.andante.subwoofers.logic.model.SubwoofersVariantInput;
import org.andante.subwoofers.logic.model.SubwoofersVariantOutput;
import org.andante.subwoofers.repository.SubwoofersRepository;
import org.andante.subwoofers.repository.entity.SubwoofersEntity;
import org.andante.subwoofers.repository.entity.SubwoofersVariantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SubwoofersVariantModelEntityMapper {

    private static final String SUBWOOFERS_NOT_FOUND_EXCEPTION_MESSAGE = "Subwoofers with identifier %d do not exist";

    private final SubwoofersRepository subwoofersRepository;

    public SubwoofersVariantOutput toModel(SubwoofersVariantEntity subwoofersVariantEntity) {
        return subwoofersVariantEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public SubwoofersVariantEntity toEntity(SubwoofersVariantInput subwoofersVariant) {
        SubwoofersEntity subwoofersEntity = subwoofersRepository.findById(subwoofersVariant.getSubwoofersId())
                .orElseThrow(() -> new SubwoofersNotFoundException(String.format(SUBWOOFERS_NOT_FOUND_EXCEPTION_MESSAGE, subwoofersVariant.getSubwoofersId())));

        return SubwoofersVariantEntity.builder()
                .id(subwoofersVariant.getId())
                .priceDifference(subwoofersVariant.getPriceDifference())
                .availableQuantity(subwoofersVariant.getAvailableQuantity())
                .imageUrl(subwoofersVariant.getImageUrl())
                .thumbnailUrl(subwoofersVariant.getThumbnailUrl())
                .color(subwoofersVariant.getColor())
                .subwoofers(subwoofersEntity)
                .build();
    }
}

package org.andante.headphones.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.headphones.logic.model.HeadphonesInput;
import org.andante.headphones.logic.model.HeadphonesOutput;
import org.andante.headphones.repository.HeadphonesVariantRepository;
import org.andante.headphones.repository.entity.HeadphonesEntity;
import org.andante.headphones.repository.entity.HeadphonesVariantEntity;
import org.andante.product.exception.ProducerNotFoundException;
import org.andante.product.repository.CommentRepository;
import org.andante.product.repository.ProducerRepository;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.product.repository.entity.ProducerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class HeadphonesModelEntityMapper {

    private static final String PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE = "Producer %s does not exist";

    private final ProducerRepository producerRepository;
    private final CommentRepository commentRepository;
    private final HeadphonesVariantRepository headphonesVariantRepository;

    public HeadphonesOutput toModel(HeadphonesEntity headphonesEntity) {
        return headphonesEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public HeadphonesEntity toEntity(HeadphonesInput headphonesInput) {
        ProducerEntity producer = producerRepository.findById(headphonesInput.getProducerName())
                .orElseThrow(() -> new ProducerNotFoundException(String.format(PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE, headphonesInput.getProducerName())));

        Set<Long> variantsIds = headphonesInput.getVariantsIds();
        Set<Long> commentsIds = headphonesInput.getCommentsIds();

        Set<CommentEntity> comments = new HashSet<>(commentRepository.findAllById(commentsIds));
        List<HeadphonesVariantEntity> headphonesVariants = headphonesVariantRepository.findAllById(variantsIds);

        return HeadphonesEntity.builder()
                .id(headphonesInput.getId())
                .name(headphonesInput.getName())
                .description(headphonesInput.getDescription())
                .weight(headphonesInput.getWeight())
                .basePrice(headphonesInput.getBasePrice())
                .productType(headphonesInput.getProductType())
                .comments(comments)
                .observers(headphonesInput.getObservers())
                .minimumFrequency(headphonesInput.getMinimumFrequency())
                .maximumFrequency(headphonesInput.getMaximumFrequency())
                .producer(producer)
                .constructionType(headphonesInput.getConstructionType())
                .driverType(headphonesInput.getDriverType())
                .wireless(headphonesInput.getWireless())
                .bluetoothStandard(headphonesInput.getBluetoothStandard())
                .variants(headphonesVariants)
                .build();
    }
}

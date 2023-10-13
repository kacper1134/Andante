package org.andante.gramophones.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.gramophones.logic.model.GramophonesInput;
import org.andante.gramophones.logic.model.GramophonesOutput;
import org.andante.gramophones.repository.GramophonesVariantRepository;
import org.andante.gramophones.repository.entity.GramophonesEntity;
import org.andante.gramophones.repository.entity.GramophonesVariantEntity;
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
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class GramophonesModelEntityMapper {

    private static final String PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE = "Producer %s does not exist";

    private final CommentRepository commentRepository;
    private final ProducerRepository producerRepository;
    private final GramophonesVariantRepository gramophonesVariantRepository;

    public GramophonesOutput toModel(GramophonesEntity gramophonesEntity) {
        return gramophonesEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public GramophonesEntity toEntity(GramophonesInput gramophonesInput) {
        ProducerEntity producer = producerRepository.findById(gramophonesInput.getProducerName())
                .orElseThrow(() -> new ProducerNotFoundException(String.format(PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE, gramophonesInput.getProducerName())));

        Set<Long> variantsIds = gramophonesInput.getVariantsIds();
        Set<Long> commentsIds = gramophonesInput.getCommentsIds();

        Set<CommentEntity> comments = new HashSet<>(commentRepository.findAllById(commentsIds));
        List<GramophonesVariantEntity> gramophoneVariants = gramophonesVariantRepository.findAllById(variantsIds);

        return GramophonesEntity.builder()
                .id(gramophonesInput.getId())
                .name(gramophonesInput.getName())
                .description(gramophonesInput.getDescription())
                .weight(gramophonesInput.getWeight())
                .basePrice(gramophonesInput.getBasePrice())
                .minimumFrequency(gramophonesInput.getMinimumFrequency())
                .maximumFrequency(gramophonesInput.getMaximumFrequency())
                .productType(gramophonesInput.getProductType())
                .comments(comments)
                .observers(gramophonesInput.getObservers())
                .producer(producer)
                .connectivityTechnology(gramophonesInput.getConnectivityTechnology())
                .turntableMaterial(gramophonesInput.getTurntableMaterial())
                .motorType(gramophonesInput.getMotorType())
                .powerSource(gramophonesInput.getPowerSource())
                .maximumRotationalSpeed(gramophonesInput.getMaximumRotationalSpeed())
                .variants(gramophoneVariants)
                .build();
    }
}

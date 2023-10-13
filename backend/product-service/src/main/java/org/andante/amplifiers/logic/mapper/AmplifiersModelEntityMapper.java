package org.andante.amplifiers.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.amplifiers.logic.model.AmplifiersInput;
import org.andante.amplifiers.logic.model.AmplifiersOutput;
import org.andante.amplifiers.repository.AmplifiersVariantRepository;
import org.andante.amplifiers.repository.entity.AmplifiersEntity;
import org.andante.amplifiers.repository.entity.AmplifiersVariantEntity;
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
public class AmplifiersModelEntityMapper {

    private static final String PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE = "Producer %s does not exist";

    private final CommentRepository commentRepository;
    private final ProducerRepository producerRepository;
    private final AmplifiersVariantRepository amplifiersVariantRepository;

    public AmplifiersOutput toModel(AmplifiersEntity amplifiersEntity) {
        return amplifiersEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public AmplifiersEntity toEntity(AmplifiersInput amplifiersInput) {
        ProducerEntity producer = producerRepository.findById(amplifiersInput.getProducerName())
                .orElseThrow(() -> new ProducerNotFoundException(String.format(PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE, amplifiersInput.getProducerName())));

        Set<Long> variantsIds = amplifiersInput.getVariantsIds();
        Set<Long> commentsIds = amplifiersInput.getCommentsIds();

        Set<CommentEntity> comments = new HashSet<>(commentRepository.findAllById(commentsIds));
        List<AmplifiersVariantEntity> amplifierVariants = amplifiersVariantRepository.findAllById(variantsIds);

        return AmplifiersEntity.builder()
                .id(amplifiersInput.getId())
                .name(amplifiersInput.getName())
                .description(amplifiersInput.getDescription())
                .weight(amplifiersInput.getWeight())
                .basePrice(amplifiersInput.getBasePrice())
                .minimumFrequency(amplifiersInput.getMinimumFrequency())
                .maximumFrequency(amplifiersInput.getMaximumFrequency())
                .productType(amplifiersInput.getProductType())
                .comments(comments)
                .observers(amplifiersInput.getObservers())
                .producer(producer)
                .power(amplifiersInput.getPower())
                .type(amplifiersInput.getAmplifierType())
                .variants(amplifierVariants)
                .build();
    }
}

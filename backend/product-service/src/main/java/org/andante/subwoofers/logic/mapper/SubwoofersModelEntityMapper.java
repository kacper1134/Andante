package org.andante.subwoofers.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.product.exception.ProducerNotFoundException;
import org.andante.product.repository.CommentRepository;
import org.andante.product.repository.ProducerRepository;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.product.repository.entity.ProducerEntity;
import org.andante.subwoofers.logic.model.SubwoofersInput;
import org.andante.subwoofers.logic.model.SubwoofersOutput;
import org.andante.subwoofers.repository.SubwoofersVariantRepository;
import org.andante.subwoofers.repository.entity.SubwoofersEntity;
import org.andante.subwoofers.repository.entity.SubwoofersVariantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SubwoofersModelEntityMapper {

    private static final String PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE = "Producer %s does not exist";

    private final CommentRepository commentRepository;
    private final ProducerRepository producerRepository;
    private final SubwoofersVariantRepository subwoofersVariantRepository;

    public SubwoofersOutput toModel(SubwoofersEntity subwoofersEntity) {
        return subwoofersEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public SubwoofersEntity toEntity(SubwoofersInput subwoofersInput) {
        ProducerEntity producer = producerRepository.findById(subwoofersInput.getProducerName())
                .orElseThrow(() -> new ProducerNotFoundException(String.format(PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE, subwoofersInput.getProducerName())));

        Set<Long> variantsIds = subwoofersInput.getVariantsIds();
        Set<Long> commentsIds = subwoofersInput.getCommentsIds();

        Set<CommentEntity> comments = new HashSet<>(commentRepository.findAllById(commentsIds));
        List<SubwoofersVariantEntity> subwoofersVariants = subwoofersVariantRepository.findAllById(variantsIds);

        return SubwoofersEntity.builder()
                .id(subwoofersInput.getId())
                .name(subwoofersInput.getName())
                .description(subwoofersInput.getDescription())
                .weight(subwoofersInput.getWeight())
                .basePrice(subwoofersInput.getBasePrice())
                .minimumFrequency(subwoofersInput.getMinimumFrequency())
                .maximumFrequency(subwoofersInput.getMaximumFrequency())
                .productType(subwoofersInput.getProductType())
                .comments(comments)
                .observers(subwoofersInput.getObservers())
                .producer(producer)
                .power(subwoofersInput.getPower())
                .type(subwoofersInput.getType())
                .variants(subwoofersVariants)
                .build();
    }
}

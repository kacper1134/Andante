package org.andante.speakers.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.product.exception.ProducerNotFoundException;
import org.andante.product.repository.CommentRepository;
import org.andante.product.repository.ProducerRepository;
import org.andante.product.repository.entity.CommentEntity;
import org.andante.product.repository.entity.ProducerEntity;
import org.andante.speakers.logic.model.SpeakersInput;
import org.andante.speakers.logic.model.SpeakersOutput;
import org.andante.speakers.repository.SpeakersVariantRepository;
import org.andante.speakers.repository.entity.SpeakersEntity;
import org.andante.speakers.repository.entity.SpeakersVariantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SpeakersModelEntityMapper {

    private static final String PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE = "Producer %s does not exist";

    private final CommentRepository commentRepository;
    private final ProducerRepository producerRepository;
    private final SpeakersVariantRepository speakersVariantRepository;

    public SpeakersOutput toModel(SpeakersEntity speakersEntity) {
        return speakersEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public SpeakersEntity toEntity(SpeakersInput speakersInput) {
        ProducerEntity producer = producerRepository.findById(speakersInput.getProducerName())
                .orElseThrow(() -> new ProducerNotFoundException(String.format(PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE, speakersInput.getProducerName())));

        Set<Long> variantsIds = speakersInput.getVariantsIds();
        Set<Long> commentsIds = speakersInput.getCommentsIds();

        Set<CommentEntity> comments = new HashSet<>(commentRepository.findAllById(commentsIds));
        List<SpeakersVariantEntity> speakersVariants = speakersVariantRepository.findAllById(variantsIds);

        return SpeakersEntity.builder()
                .id(speakersInput.getId())
                .name(speakersInput.getName())
                .description(speakersInput.getDescription())
                .weight(speakersInput.getWeight())
                .basePrice(speakersInput.getBasePrice())
                .minimumFrequency(speakersInput.getMinimumFrequency())
                .maximumFrequency(speakersInput.getMaximumFrequency())
                .productType(speakersInput.getProductType())
                .comments(comments)
                .observers(speakersInput.getObservers())
                .producer(producer)
                .wireless(speakersInput.getWireless())
                .bluetoothStandard(speakersInput.getBluetoothStandard())
                .variants(speakersVariants)
                .build();
    }
}

package org.andante.microphones.logic.mapper;

import lombok.RequiredArgsConstructor;
import org.andante.microphones.logic.model.MicrophonesInput;
import org.andante.microphones.logic.model.MicrophonesOutput;
import org.andante.microphones.repository.MicrophonesVariantRepository;
import org.andante.microphones.repository.entity.MicrophonesEntity;
import org.andante.microphones.repository.entity.MicrophonesVariantEntity;
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
public class MicrophonesModelEntityMapper {

    private static final String PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE = "Producer %s does not exist";

    private final CommentRepository commentRepository;
    private final ProducerRepository producerRepository;
    private final MicrophonesVariantRepository microphonesVariantRepository;

    public MicrophonesOutput toModel(MicrophonesEntity microphonesEntity) {
        return microphonesEntity.toModel();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public MicrophonesEntity toEntity(MicrophonesInput microphonesInput) {
        ProducerEntity producer = producerRepository.findById(microphonesInput.getProducerName())
                .orElseThrow(() -> new ProducerNotFoundException(String.format(PRODUCER_NOT_FOUND_EXCEPTION_MESSAGE, microphonesInput.getProducerName())));

        Set<Long> variantsIds = microphonesInput.getVariantsIds();
        Set<Long> commentsIds = microphonesInput.getCommentsIds();

        Set<CommentEntity> comments = new HashSet<>(commentRepository.findAllById(commentsIds));
        List<MicrophonesVariantEntity> microphonesVariants = microphonesVariantRepository.findAllById(variantsIds);

        return MicrophonesEntity.builder()
                .id(microphonesInput.getId())
                .name(microphonesInput.getName())
                .description(microphonesInput.getDescription())
                .weight(microphonesInput.getWeight())
                .basePrice(microphonesInput.getBasePrice())
                .minimumFrequency(microphonesInput.getMinimumFrequency())
                .maximumFrequency(microphonesInput.getMaximumFrequency())
                .productType(microphonesInput.getProductType())
                .comments(comments)
                .observers(microphonesInput.getObservers())
                .producer(producer)
                .wireless(microphonesInput.getWireless())
                .bluetoothStandard(microphonesInput.getBluetoothStandard())
                .type(microphonesInput.getType())
                .variants(microphonesVariants)
                .build();
    }
}

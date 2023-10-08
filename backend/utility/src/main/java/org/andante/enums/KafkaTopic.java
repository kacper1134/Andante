package org.andante.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KafkaTopic {
    PRODUCT_AMPLIFIER_INTERNAL_TOPIC("event.Internal.Product.Amplifier.json"),
    PRODUCT_AMPLIFIER_VARIANT_INTERNAL_TOPIC("event.Internal.Product.Amplifier.Variant.json"),
    PRODUCT_GRAMOPHONE_INTERNAL_TOPIC("event.Internal.Product.Gramophone.json"),
    PRODUCT_GRAMOPHONE_VARIANT_INTERNAL_TOPIC("event.Internal.Product.Gramophone.Variant.json"),
    PRODUCT_HEADPHONES_INTERNAL_TOPIC("event.Internal.Product.Headphones.json"),
    PRODUCT_HEADPHONES_VARIANT_INTERNAL_TOPIC("event.Internal.Product.Headphones.Variant.json"),
    PRODUCT_MICROPHONE_INTERNAL_TOPIC("event.Internal.Product.Microphone.json"),
    PRODUCT_MICROPHONE_VARIANT_INTERNAL_TOPIC("event.Internal.Product.Microphone.Variant.json"),
    PRODUCT_SPEAKERS_INTERNAL_TOPIC("event.Internal.Product.Speakers.json"),
    PRODUCT_SPEAKERS_VARIANT_INTERNAL_TOPIC("event.Internal.Product.Speakers.Variant.json"),
    PRODUCT_SUBWOOFERS_INTERNAL_TOPIC("event.Internal.Product.Subwoofers.json"),
    PRODUCT_SUBWOOFERS_VARIANT_INTERNAL_TOPIC("event.Internal.Product.Subwoofers.Variant.json"),
    PRODUCT_COMMENT_INTERNAL_TOPIC("event.Internal.Product.Comment.json"),
    PRODUCT_PRODUCER_INTERNAL_TOPIC("event.Internal.Product.Producer.json"),
    PRODUCT_ORDER_INTERNAL_TOPIC("event.Internal.Product.Order.json"),
    ORDER_INTERNAL_TOPIC("event.Internal.Order.json"),
    ORDER_ENTRY_INTERNAL_TOPIC("event.Internal.Order.OrderEntry.json");

    private final String topicName;
}

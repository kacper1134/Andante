package org.andante.product.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.andante.enums.OperationType;
import org.andante.product.dto.CommentDTO;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent {
    private CommentDTO comment;
    private OperationType operationType;
}

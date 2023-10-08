package org.andante.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentStatistics {

    private String username;
    private Integer commentsCount;
    private Integer upvoteCount;
}

package org.andante.forum.logic.service;

import dto.response.PostResponseQuerySpecification;
import org.andante.enums.OperationStatus;
import org.andante.forum.logic.model.response.PostResponseInputModel;
import org.andante.forum.logic.model.response.PostResponseOutputModel;
import org.andante.forum.logic.model.response.PostResponsesLikesRelationModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostResponseService {

    Page<PostResponseOutputModel> getByQuery(PostResponseQuerySpecification postResponseQuerySpecification);
    Long create(PostResponseInputModel postResponseInputModel);
    PostResponseOutputModel likeResponse(PostResponsesLikesRelationModel postResponsesLikesRelationModel);
    PostResponseOutputModel get(long id);
    OperationStatus delete(long id);
    OperationStatus update(PostResponseInputModel postResponseInputModel);
}

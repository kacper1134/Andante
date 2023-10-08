package org.andante.forum.logic.service;

import dto.post.PostQuerySpecification;
import dto.post.TopQuerySpecification;
import org.andante.enums.OperationStatus;
import org.andante.forum.logic.model.post.PostInputModel;
import org.andante.forum.logic.model.post.PostLikesRelationModel;
import org.andante.forum.logic.model.post.PostOutputModel;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface PostService {

    Long create(PostInputModel post);
    Page<PostOutputModel> getByQuery(PostQuerySpecification postQuerySpecification);
    List<PostOutputModel> getTopPage(TopQuerySpecification topQuerySpecification);
    Set<PostOutputModel> getLikedByUser(String emailAddress);
    PostOutputModel likePost(PostLikesRelationModel postLikesRelationModel);
    PostOutputModel getPost(long id);
    OperationStatus delete(long id);
    OperationStatus update(PostInputModel postModel);
}

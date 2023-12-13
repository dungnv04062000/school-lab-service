package com.schoollab.repository;

import com.schoollab.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    void deleteAllByCommentId(String commentId);
}

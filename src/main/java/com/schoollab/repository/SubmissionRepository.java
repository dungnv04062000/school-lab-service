package com.schoollab.repository;

import com.schoollab.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, String> {
    Submission findAllByFromIdAndLessonId(String fromId, String lessonId);
}

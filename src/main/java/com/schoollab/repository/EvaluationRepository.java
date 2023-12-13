package com.schoollab.repository;

import com.schoollab.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, String> {
    Evaluation findByFromIdAndToIdAndLessonId(String fromId, String toId, String lessonId);

    void deleteAllByLessonId(String lessonId);
}

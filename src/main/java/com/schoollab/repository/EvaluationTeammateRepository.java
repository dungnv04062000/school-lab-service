package com.schoollab.repository;

import com.schoollab.model.EvaluationGroup;
import com.schoollab.model.EvaluationTeammate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationTeammateRepository extends JpaRepository<EvaluationTeammate, String> {

    EvaluationTeammate findByFromIdAndToIdAndLessonId(String fromId, String toId, String lessonId);

    void deleteAllByLessonId(String lessonId);
}

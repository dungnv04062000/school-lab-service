package com.schoollab.repository;

import com.schoollab.model.Evaluation;
import com.schoollab.model.EvaluationGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationGroupRepository extends JpaRepository<EvaluationGroup, String> {

    EvaluationGroup findByFromIdAndGroupIdAndLessonId(String fromId, String groupId, String lessonId);

    void deleteAllByLessonId(String lessonId);
}

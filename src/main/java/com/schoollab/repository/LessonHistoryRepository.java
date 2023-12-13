package com.schoollab.repository;

import com.schoollab.model.LessonHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface LessonHistoryRepository extends JpaRepository<LessonHistory, String> {
    Integer deleteAllByLessonId(String lessonId);
}

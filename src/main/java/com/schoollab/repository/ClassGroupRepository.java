package com.schoollab.repository;

import com.schoollab.model.ClassGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassGroupRepository extends JpaRepository<ClassGroup, String> {

    List<ClassGroup> findAllByClassIdAndLessonId(String classId, String lessonId);

    List<ClassGroup> findAllByLessonId(String lessonId);
}

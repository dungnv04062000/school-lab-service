package com.schoollab.repository;

import com.schoollab.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {

    Lesson findByTitle(String title);
}

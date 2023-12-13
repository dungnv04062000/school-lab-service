package com.schoollab.repository;

import com.schoollab.model.Note;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
@Repository
public interface NoteRepository extends JpaRepository<Note, String> {
    @Query("SELECT n FROM Note n WHERE n.userId = :userId AND n.lessonId = :lessonId")
    Note findNoteLessonByUser(@Param("userId") String userId,@Param("lessonId") String lessonId);

    Note findNoteByUserId(String userId);

    Note findByUserIdAndLessonId(String userId, String lessonId);
}

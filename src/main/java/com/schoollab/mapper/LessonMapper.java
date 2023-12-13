package com.schoollab.mapper;

import com.schoollab.dto.LessonDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.Instant;
import java.util.List;

@Mapper
public interface LessonMapper {

    List<LessonDto> getAllLessons(@Param("class_id") String classId,
                                  @Param("teacher_id") String teacherId,
                                  @Param("level_id") Integer levelId,
                                  @Param("subject_id") String subjectId,
                                  @Param("title") String title,
                                  @Param("create_at_from") Instant createAtFrom,
                                  @Param("create_at_to") Instant createAtTo,
                                  @Param("page") int page,
                                  @Param("row_number") int rowNumber);

    int countAllLessons(@Param("class_id") String classId,
                                  @Param("teacher_id") String teacherId,
                                    @Param("level_id") Integer levelId,
                                  @Param("subject_id") String subjectId,
                                  @Param("title") String title,
                                  @Param("create_at_from") Instant createAtFrom,
                                  @Param("create_at_to") Instant createAtTo);

    List<LessonDto> getAllLessonsForStudent(@Param("class_id") String classId,
                                  @Param("subject_id") String subjectId,
                                  @Param("title") String title,
                                  @Param("level_id") Integer levelId,
                                  @Param("create_at_from") Instant createAtFrom,
                                  @Param("create_at_to") Instant createAtTo,
                                  @Param("page") int page,
                                  @Param("row_number") int rowNumber);

    int countAllLessonsForStudent(@Param("class_id") String classId,
                                  @Param("subject_id") String subjectId,
                                  @Param("title") String title,
                                  @Param("level_id") Integer levelId,
                                  @Param("create_at_from") Instant createAtFrom,
                                  @Param("create_at_to") Instant createAtTo);

    LessonDto getOne(@Param("id") String lessonId);
}

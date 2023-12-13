package com.schoollab.mapper;

import com.schoollab.dto.SubmissionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.Instant;
import java.util.List;

@Mapper
public interface SubmissionMapper {

    List<SubmissionDto> getAll(@Param("teacher_id") String teacherId,
                                @Param("class_id") String classId,
                                @Param("semester_id") String semesterId,
                               @Param("lesson_title") String lessonTitle,
                               @Param("student") String student,
                               @Param("create_at_from") Instant createAtFrom,
                               @Param("create_at_to") Instant createAtTo,
                               @Param("order_by") String orderBy,
                               @Param("page") int page,
                               @Param("row_number") int rowNumber);
    int countAll(@Param("teacher_id") String teacherId,
                 @Param("class_id") String classId,
                 @Param("semester_id") String semesterId,
                 @Param("lesson_title") String lessonTitle,
                 @Param("student") String student,
                 @Param("create_at_from") Instant createAtFrom,
                 @Param("create_at_to") Instant createAtTo);

    List<SubmissionDto> getOwnerSubmissions(@Param("user_id") String userId,
                                            @Param("semester_id") String semesterId,
                               @Param("lesson_title") String lessonTitle,
                               @Param("create_at_from") Instant createAtFrom,
                               @Param("create_at_to") Instant createAtTo,
                               @Param("order_by") String orderBy,
                               @Param("page") int page,
                               @Param("row_number") int rowNumber);
    int countOwnerSubmissions(@Param("user_id") String userId,
                              @Param("semester_id") String semesterId,
                              @Param("lesson_title") String lessonTitle,
                              @Param("create_at_from") Instant createAtFrom,
                              @Param("create_at_to") Instant createAtTo);

    SubmissionDto getOne(@Param("submission_id") String submissionId);
}

package com.schoollab.mapper;

import com.schoollab.dto.LessonGradeDto;
import com.schoollab.dto.OwnerGradeStatisticDto;
import com.schoollab.dto.SemesterGradeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EvaluationMapper {

    List<LessonGradeDto> getAllStudentGrade(@Param("lesson_id") String lessonId);

    List<OwnerGradeStatisticDto> getOwnerGradeStatistic(@Param("user_id") String studentId,
                                                        @Param("semester_id") String semesterId,
                                                        @Param("subject_id") String subjectId);

    OwnerGradeStatisticDto getFinalGrades(@Param("user_id") String studentId,
                                          @Param("class_id") String classId,
                                          @Param("subject_id") String subjectId);
}

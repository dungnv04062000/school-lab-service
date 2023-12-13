package com.schoollab.service;

import com.schoollab.dto.EvaluationDto;
import com.schoollab.dto.LessonGradeDto;
import com.schoollab.dto.OwnerGradeStatisticDto;
import com.schoollab.dto.SemesterGradeDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EvaluationService {

    EvaluationDto evaluateStudent(String fromId, String toId, String lessonId, Float grade);

    List<EvaluationDto> evaluateStudentByTemplate(String fromId, String lessonId, MultipartFile multipartFile);

    List<EvaluationDto> evaluateByGroup(String fromId, String groupId, String lessonId, Float mark);

    List<LessonGradeDto> getEvaluationGradeList(String lessonId);

    LessonGradeDto getOneGrade(String studentId, String lessonId);

    List<OwnerGradeStatisticDto> getOwnerGradeStatistic(String studentId, String semesterId, String subjectId);

    String downloadEvaluationTemplate(String lessonId, Boolean isResult);

    String downloadClassGrade(String classId, String subjectId);

    List<SemesterGradeDto> getClassGrade(String classId, String subjectId);
}

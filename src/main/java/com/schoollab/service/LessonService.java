package com.schoollab.service;

import com.schoollab.controller.request.LessonCreateRequestBody;
import com.schoollab.controller.request.LessonUpdateRequestBody;
import com.schoollab.dto.LessonDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

public interface LessonService {
    LessonDto createLesson(String createBy, LessonCreateRequestBody requestBody, MultipartFile multipartFile);

    LessonDto copyLessonToOtherClass(String lessonId, String classId);

    LessonDto updateLesson(String lessonId, LessonUpdateRequestBody requestBody, MultipartFile multipartFile);

    LessonDto finishingLesson(String lessonId);

    LessonDto reOpenningLesson(String lessonId, Instant endAt);

    LessonDto deleteLesson(String lessonId);

    List<LessonDto> getAllLessons(String classId, String userId, Integer levelId, String subjectId, String title, Instant createAtFrom, Instant createAtTo, Integer page, Integer rowNumber);

    int countAllLessons(String classId, String userId, Integer levelId, String subjectId, String title, Instant createAtFrom, Instant createAtTo);

    List<LessonDto> getAllLessonsForStudent(String semesterId, String subjectId, String title, Integer levelId, Instant createAtFrom, Instant createAtTo, Integer page, Integer rowNumber);

    int countAllLessonsForStudent(String semesterId, String subjectId, String title, Integer levelId, Instant createAtFrom, Instant createAtTo);

    LessonDto getOne(String id);
}

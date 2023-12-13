package com.schoollab.service;

import com.schoollab.dto.SubmissionDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

public interface SubmissionService {

    SubmissionDto createSubmission(String fromId, String lessonId, String content, MultipartFile multipartFile);

    SubmissionDto editSubmission(String submissionId, String content, MultipartFile multipartFile);

    SubmissionDto getSubmissionByFromIdAndLessonId(String userId, String lessonId);

    SubmissionDto getOne(String submissionId);

    List<SubmissionDto> getAll(String userId, String classId, String semesterId, String lessonTitle, String student, Instant createAtFrom, Instant createAtTo, String orderBy, Integer page, Integer rowNumber);

    int countAll(String userId, String classId, String semesterId, String lessonTitle, String student, Instant createAtFrom, Instant createAtTo);

    List<SubmissionDto> getOwnerSubmissions(String userId, String semesterId, String lessonTitle, Instant createAtFrom, Instant createAtTo, String orderBy, Integer page, Integer rowNumber);

    int countOwnerSubmissions(String userId, String semesterId, String lessonTitle, Instant createAtFrom, Instant createAtTo);

}

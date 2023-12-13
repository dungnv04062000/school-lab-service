package com.schoollab.service;

import com.schoollab.controller.request.SubjectAddRequest;
import com.schoollab.dto.SubjectDto;

import java.util.List;

public interface SubjectService {
    SubjectDto saveSubject(SubjectAddRequest req);

    SubjectDto getOneSubject(int subjectId);

    List<SubjectDto> getAllSubject();

    List<SubjectDto> filterSubject(String userId, String subjectName);

    String deleteSubject(int subjectId);
}

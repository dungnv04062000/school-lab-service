package com.schoollab.service;

import com.schoollab.controller.request.SemesterCreateRequestBody;
import com.schoollab.controller.request.SemesterUpdateRequestBody;
import com.schoollab.dto.SemesterDto;

import java.util.List;

public interface SemesterService {

    List<SemesterDto> getSemesters(String userId, String semesterName, Integer year);

    SemesterDto getOne(String semesterId);

    SemesterDto createSemester(SemesterCreateRequestBody requestBody);

    SemesterDto updateSemester(String semesterId, SemesterUpdateRequestBody requestBody);

    void deleteSemester(String semesterId);
}

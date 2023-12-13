package com.schoollab.service;

import com.schoollab.controller.request.ClassCreateRequestBody;
import com.schoollab.controller.request.ClassUpdateRequestBody;
import com.schoollab.dto.ClassDto;

import java.util.List;

public interface ClassService {

    ClassDto createClass(ClassCreateRequestBody requestBody);

    void deleteClass(String classId);

    ClassDto updateClass(String classId, ClassUpdateRequestBody requestBody);

    List<ClassDto> getAll(String teacherId, String semesterId, String gradeId, String className, String formTeacher);

    List<ClassDto> getAllForAdminSchool(String campusId, String semesterId, String gradeId, String className, String formTeacher);

    ClassDto getOne(String id);
}

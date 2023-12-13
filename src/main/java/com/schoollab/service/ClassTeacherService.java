package com.schoollab.service;

import com.schoollab.controller.request.ClassTeacherRequest;
import com.schoollab.dto.ClassTeacherDto;
import com.schoollab.dto.StudentInClassDto;
import com.schoollab.dto.TeacherInClassDto;

import java.util.List;

public interface ClassTeacherService {
    ClassTeacherDto addTeacherToClass(ClassTeacherRequest request);

    List<TeacherInClassDto> filterTeacherInClass(String classId, String teacher);

    String removeTeacherInClass(String teacherId);

    List<TeacherInClassDto> filterTeacherInCampus(String campusId, String semesterId, Integer roleId, String className, String teacher, String gender, Integer page, Integer rowNumber);

    List<TeacherInClassDto> getAllTeachers(String campusId, String teacher, String gender, Integer page, Integer rowNumber);

    Integer countAllTeachers(String campusId, String teacher, String gender);

    int countAllTeacherInCampus(String campusId, String semesterId, Integer roleId, String className, String student, String gender);
}

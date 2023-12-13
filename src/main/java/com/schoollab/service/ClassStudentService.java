package com.schoollab.service;

import com.schoollab.controller.request.ClassManyStudentRequest;
import com.schoollab.controller.request.ClassStudentAddRequestBody;
import com.schoollab.dto.ClassStudentDto;
import com.schoollab.dto.StudentInClassDto;
import com.schoollab.dto.UserDto;

import java.util.List;

public interface ClassStudentService {
    ClassStudentDto saveStudentToClass(ClassStudentAddRequestBody req);

    List<ClassStudentDto> saveManyStudentToClass(ClassManyStudentRequest req);

    void removeStudent(String classStudentId);

    List<StudentInClassDto> searchAllStudent(String campusId, String student, String gender, Integer page, Integer rowNumber);

    int countAllStudent(String campusId, String student, String gender);

    List<StudentInClassDto> filterStudent(String classId, String student, String gender);

    List<StudentInClassDto> filterStudentInCampus(String campusId, String semesterId, Integer gradeId, Integer roleId, String className, String student, String gender, Integer page, Integer rowNumber);

    int countAllStudentInCampus(String campusId, String semesterId, Integer gradeId, Integer roleId, String className, String student, String gender);

}

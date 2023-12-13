package com.schoollab.mapper;

import com.schoollab.dto.ClassDto;
import com.schoollab.dto.ClassStudentDto;
import com.schoollab.dto.StudentInClassDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassStudentMapper {
    List<StudentInClassDto> getAll(@Param("class_id") String classId,
                                   @Param("student") String student,
                                   @Param("gender") String gender);

    List<StudentInClassDto> getAllInCampus(@Param("campus_id") String campusId,
                                           @Param("semester_id") String semesterId,
                                           @Param("grade_id") Integer gradeId,
                                           @Param("role_id") int roleId,
                                           @Param("class_name") String className,
                                           @Param("student") String student,
                                           @Param("gender") String gender,
                                           @Param("page") int page,
                                           @Param("row_number") int rowNumber);

    int countAllInCampus(@Param("campus_id") String campusId,
                         @Param("semester_id") String semesterId,
                         @Param("grade_id") Integer gradeId,
                         @Param("role_id") int roleId,
                         @Param("class_name") String className,
                         @Param("student") String student,
                         @Param("gender") String gender);

    List<StudentInClassDto> getAllByCampusForRootAdmin(@Param("campus_id") String campusId,
                                           @Param("role_id") int roleId,
                                           @Param("student") String student,
                                           @Param("gender") String gender,
                                           @Param("page") int page,
                                           @Param("row_number") int rowNumber);

    int countAllByCampusForRootAdmin(@Param("campus_id") String campusId,
                         @Param("role_id") int roleId,
                         @Param("student") String student,
                         @Param("gender") String gender);
}

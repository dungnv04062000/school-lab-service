package com.schoollab.mapper;

import com.schoollab.dto.StudentInClassDto;
import com.schoollab.dto.TeacherInClassDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassTeacherMapper {
    List<TeacherInClassDto> getAllTeacher(@Param("class_id") String classId,
                                          @Param("teacher") String teacher);

    List<TeacherInClassDto> getAllInCampus(@Param("campus_id") String campusId,
                                           @Param("semester_id") String semesterId,
                                           @Param("role_id") int roleId,
                                           @Param("class_name") String className,
                                           @Param("teacher") String teacher,
                                           @Param("gender") String gender,
                                           @Param("page") int page,
                                           @Param("row_number") int rowNumber);

    int countAllInCampus(@Param("campus_id") String campusId,
                         @Param("semester_id") String semesterId,
                         @Param("role_id") int roleId,
                         @Param("class_name") String className,
                         @Param("teacher") String teacher,
                         @Param("gender") String gender);

    List<TeacherInClassDto> getAllInCampusForRootAdmin(@Param("campus_id") String campusId,
                                           @Param("role_id") int roleId,
                                           @Param("teacher") String teacher,
                                           @Param("gender") String gender,
                                           @Param("page") int page,
                                           @Param("row_number") int rowNumber);

    int countAllInCampusForRootAdmin(@Param("campus_id") String campusId,
                         @Param("role_id") int roleId,
                         @Param("teacher") String teacher,
                         @Param("gender") String gender);
}

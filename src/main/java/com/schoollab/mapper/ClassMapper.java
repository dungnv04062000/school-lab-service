package com.schoollab.mapper;

import com.schoollab.dto.ClassDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassMapper {

    List<ClassDto> getAll(@Param("campus_id") String campusId,
                          @Param("semester_id") String semesterId,
                          @Param("grade_id") String gradeId,
                          @Param("teacher_id") String teacherId,
                          @Param("class_name") String className,
                          @Param("form_teacher") String formTeacherName);

    ClassDto getOne(@Param("class_id") String classId);
}

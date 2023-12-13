package com.schoollab.mapper;

import com.schoollab.dto.ClassGroupDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClassGroupMapper {

    ClassGroupDto getOwnerGroup(@Param("lesson_id") String lessonId,
                                @Param("student_id") String studentId);
}

package com.schoollab.mapper;

import com.schoollab.dto.SemesterDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SemesterMapper {

    List<SemesterDto> getSemesters(@Param("campus_id") String campusId,
                                   @Param("name") String semesterName,
                                   @Param("year") Integer year);
}

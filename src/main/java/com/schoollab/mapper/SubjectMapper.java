package com.schoollab.mapper;

import com.schoollab.dto.SubjectDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubjectMapper {
    List<SubjectDto> getAllSubject(@Param("user_id") String userId,
                                   @Param("subject_name") String subjectName
    );

    SubjectDto getOneSubject(@Param("subject_id") int subjectId);
}

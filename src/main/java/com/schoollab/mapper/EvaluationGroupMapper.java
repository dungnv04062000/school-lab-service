package com.schoollab.mapper;

import com.schoollab.dto.EvaluationGroupDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EvaluationGroupMapper {

    List<EvaluationGroupDto> getAllEvaluationGroups(@Param("lesson_id") String lessonId,
                                                    @Param("from_id") String fromId);
}

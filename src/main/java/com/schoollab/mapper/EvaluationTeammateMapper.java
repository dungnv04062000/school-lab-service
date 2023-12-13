package com.schoollab.mapper;

import com.schoollab.dto.EvaluationTeammateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EvaluationTeammateMapper {

    List<EvaluationTeammateDto> getAllEvaluationTeammates(@Param("lesson_id") String lessonId,
                                                          @Param("from_id") String fromId);
}

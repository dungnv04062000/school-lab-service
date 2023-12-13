package com.schoollab.mapper;

import com.schoollab.dto.TutorialDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.Instant;
import java.util.List;

@Mapper
public interface TutorialMapper {
    List<TutorialDto> getAll(@Param("campus_id") String campusId,
                             @Param("create_by") String createBy,
                             @Param("title") String title,
                             @Param("create_at_from") Instant createAtFrom,
                             @Param("create_at_to") Instant createAtTo,
                             @Param("page") int page,
                             @Param("row_number") int rowNumber);

    int countAll(@Param("campus_id") String campusId,
                 @Param("create_by") String createBy,
                 @Param("title") String title,
                 @Param("create_at_from") Instant createAtFrom,
                 @Param("create_at_to") Instant createAtTo);
}

package com.schoollab.mapper;

import com.schoollab.dto.SupportUserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.Instant;
import java.util.List;

@Mapper
public interface SupportMapper {
    List<SupportUserDto> getAllSupportsRequest(@Param("priority") String priority,
                                               @Param("status") String status,
                                               @Param("create_at_from") Instant createAtFrom,
                                               @Param("create_at_to") Instant createAtTo,
                                               @Param("user_id")String userId,
                                               @Param("type_sort") String type,
                                               @Param("page") Integer page,
                                               @Param("row_number") Integer rowNumber
    );

    Integer countAllSupportsRequest(@Param("priority") String priority,
                                               @Param("status") String status,
                                               @Param("create_at_from") Instant createAtFrom,
                                               @Param("create_at_to") Instant createAtTo,
                                                @Param("user_id")String userId);
}

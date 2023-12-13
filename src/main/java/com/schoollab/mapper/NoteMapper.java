package com.schoollab.mapper;

import com.schoollab.dto.NoteDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.Instant;
import java.util.List;
@Mapper
public interface NoteMapper {
    List<NoteDto> getAll(@Param("user_id") String userId,
                         @Param("lesson_title") String lessonTitle,
                         @Param("create_at_from") Instant createAtFrom,
                         @Param("create_at_to") Instant createAtTo,
                         @Param("page") int page,
                         @Param("row_number") int rowNumber,
                         @Param("order_by") String orderBy);

    int countAllNote(@Param("user_id") String userId,
                     @Param("lesson_title") String lessonTitle,
                     @Param("create_at_from") Instant createAtFrom,
                     @Param("create_at_to") Instant createAtTo);

    NoteDto getOne(@Param("id") String id);

    NoteDto getOneByLessonId(@Param("user_id") String userId,
            @Param("lesson_id") String lessonId);
}

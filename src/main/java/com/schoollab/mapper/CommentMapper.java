package com.schoollab.mapper;

import com.schoollab.dto.CommentDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<CommentDto> getComments(
            @Param("lesson_id") String lessonId,
            @Param("from_id") String fromId,
            @Param("page") Integer page,
            @Param("row_number") Integer rowNumber
    );

    List<CommentDto> getReplies(
            @Param("comment_id") String commentId,
            @Param("from_id") String fromId
    );

    int countComments(@Param("lesson_id") String lessonId);

    int countDirectComments(@Param("lesson_id") String lessonId);
}

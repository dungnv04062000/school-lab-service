package com.schoollab.service;

import com.schoollab.controller.request.CommentCreateRequestBody;
import com.schoollab.controller.request.CommentUpdateRequestBody;
import com.schoollab.dto.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(String fromId, CommentCreateRequestBody requestBody);

    CommentDto editComment(String commentId, CommentUpdateRequestBody requestBody);

    void deleteComment(String commentId);

    List<CommentDto> getComments(String lessonId, String fromId, int page);
}

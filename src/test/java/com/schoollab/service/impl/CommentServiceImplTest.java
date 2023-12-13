package com.schoollab.service.impl;

import com.schoollab.common.constants.PageConstant;
import com.schoollab.controller.request.CommentCreateRequestBody;
import com.schoollab.controller.request.CommentUpdateRequestBody;
import com.schoollab.dto.CommentDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.CommentMapper;
import com.schoollab.model.Comment;
import com.schoollab.model.Lesson;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.CommentRepository;
import com.schoollab.repository.LessonRepository;
import com.schoollab.repository.UserRepository;
import org.hibernate.validator.constraints.ru.INN;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class CommentServiceImplTest {
    @InjectMocks
    CommentServiceImpl commentService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    LessonRepository lessonRepository;
    @Mock
    CommentMapper commentMapper;
    @Mock
    GenericMapper genericMapper;
    CommentDto commentDto;
    Comment comment;
    Lesson lesson;
    CommentCreateRequestBody commentCreateRequestBody;
    CommentUpdateRequestBody commentUpdateRequestBody;
    List<CommentDto> commentDtos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        commentDto = SetUpModelTest.setUpCommentDto();
        comment = SetUpModelTest.setUpComment();
        lesson = SetUpModelTest.setUpLesson();
        commentCreateRequestBody = SetUpModelTest.commentReq();
        commentUpdateRequestBody = SetUpModelTest.commentUpdateReq();
        commentDtos = new ArrayList<>();
        commentDtos.add(commentDto);
    }

    @Test
    void createComment_NotFoundLesson() {
        when(lessonRepository.findById(Mockito.eq("123"))).thenReturn(Optional.ofNullable(null));
        CommentDto response = null;
        try {
            response = commentService.createComment("HE140705", commentCreateRequestBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy dự án này");
        }
    }

    @Test
    void createComment_NotFoundComment() {
        when(lessonRepository.findById(Mockito.eq("123"))).thenReturn(Optional.ofNullable(lesson));
        when(commentRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(comment));
        CommentDto response = null;
        try {
            response = commentService.createComment("HE140705", commentCreateRequestBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Bình luận này không tồn tại hoặc đã bị xóa");
        }
    }

    @Test
    void createComment_success() {
        when(lessonRepository.findById(Mockito.eq("123"))).thenReturn(Optional.ofNullable(lesson));
        when(commentRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(comment));
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(CommentDto.class))).thenReturn(commentDto);
        CommentDto response = commentService.createComment("HE140705", commentCreateRequestBody);

        assertNotNull(response);
        assertEquals(response, commentDto);
    }

    @Test
    void editComment_NotFoundComment() {
        when(commentRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        CommentDto response = null;
        try {
            response = commentService.editComment("123", commentUpdateRequestBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy bình luận này");
            assertNull(response);
        }
    }

    @Test
    void deleteComment_NotFoundComment() {
        when(commentRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
        CommentDto response = null;
        try {
            response = commentService.editComment("123", commentUpdateRequestBody);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertEquals(e.getMessage(), "Không tìm thấy bình luận này");
            assertNull(response);
        }
    }

    @Test
    void getComments() {
        when(commentMapper.getComments(Mockito.any(), Mockito.any(), Mockito.eq(1), Mockito.eq(10))).thenReturn(commentDtos);
        when(commentMapper.getReplies(Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());

        List<CommentDto> response = commentService.getComments("1234", "HE140705", 1);
        assertNotNull(response);
        assertEquals(commentDtos, response);
    }

    @Test
    void setCommentReplies() {
    }

    @Test
    void deleteReplies_() {
        when(commentMapper.getReplies(Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
        commentService.deleteReplies("1234", "HE140705");
    }
}
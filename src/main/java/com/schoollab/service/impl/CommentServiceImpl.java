package com.schoollab.service.impl;

import com.schoollab.common.constants.PageConstant;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.CommentCreateRequestBody;
import com.schoollab.controller.request.CommentUpdateRequestBody;
import com.schoollab.dto.CommentDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.mapper.CommentMapper;
import com.schoollab.model.Comment;
import com.schoollab.model.Lesson;
import com.schoollab.repository.CommentRepository;
import com.schoollab.repository.LessonRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    GenericMapper genericMapper;

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public CommentDto createComment(String fromId, CommentCreateRequestBody requestBody) {
        Optional<Lesson> optLesson = lessonRepository.findById(requestBody.getLessonId());
        if(!optLesson.isPresent()){
            throw new NotFoundException("Không tìm thấy dự án này");
        }
        if(requestBody.getCommentId() != null){
            Optional<Comment> optComment = commentRepository.findById(requestBody.getCommentId());
            if(!optComment.isPresent()){
                throw new NotFoundException("Bình luận này không tồn tại hoặc đã bị xóa");
            }
        }

        Comment comment = new Comment()
                .setLessonId(requestBody.getLessonId())
                .setFromId(fromId)
                .setCommentId(requestBody.getCommentId())
                .setContent(requestBody.getContent())
                .setCreateAt(Instant.now());

        Comment savedComment = commentRepository.save(comment);
        return genericMapper.mapToTypeNotNullProperty(savedComment, CommentDto.class);
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public CommentDto editComment(String commentId, CommentUpdateRequestBody requestBody) {
        Optional<Comment> optComment = commentRepository.findById(commentId);
        if(!optComment.isPresent()){
            throw new NotFoundException("Không tìm thấy bình luận này");
        }
        Comment comment = optComment.get();
        if(!comment.getFromId().equals(UserInfoUtil.getUserID())){
            throw new BadRequestException("Bạn không thể sửa bình luận của người khác");
        }
        comment.setContent(requestBody.getContent());
        comment.setUpdateAt(Instant.now());
        Comment savedComment = commentRepository.save(comment);
        return genericMapper.mapToTypeNotNullProperty(savedComment, CommentDto.class);
    }

    @Override
    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    public void deleteComment(String commentId) {
        Optional<Comment> optComment = commentRepository.findById(commentId);
        if(!optComment.isPresent()){
            throw new NotFoundException("Không tìm thấy bình luận này");
        }
        Comment comment = optComment.get();
        if(!comment.getFromId().equals(UserInfoUtil.getUserID())){
            throw new BadRequestException("Bạn không thể xóa bình luận của người khác");
        }

        deleteReplies(comment.getId(), comment.getFromId());
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> getComments(String lessonId, String fromId, int page) {

        List<CommentDto> directComments = commentMapper
                .getComments(lessonId, fromId, page, PageConstant.COMMENT_ROW_NUMBER);

        for (CommentDto comment : directComments){
            setCommentReplies(comment, fromId);
        }
        return directComments;
    }

    //hàm đệ quy lấy danh sách các replies
    void setCommentReplies(CommentDto comment, String fromId){
        List<CommentDto> replies = commentMapper.getReplies(comment.getId(), fromId);
        for (CommentDto item : replies) {
            setCommentReplies(item, fromId);
        }
        comment.setReplies(replies);
    }

    @Transactional(rollbackFor = {Throwable.class, Exception.class})
    void deleteReplies(String commentId, String fromId){
        List<CommentDto> replies = commentMapper.getReplies(commentId, fromId);
        if(replies != null && !replies.isEmpty()){
            for (CommentDto item : replies) {
                deleteReplies(item.getId(), fromId);
            }
        }
        commentRepository.deleteAllByCommentId(commentId);
    }
}

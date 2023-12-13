package com.schoollab.controller.api.v1;

import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.CommentCreateRequestBody;
import com.schoollab.controller.request.CommentUpdateRequestBody;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.dto.CommentDto;
import com.schoollab.mapper.CommentMapper;
import com.schoollab.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class CommentController {

    @Autowired
    CommentService commentService;

    @Autowired
    CommentMapper commentMapper;

    @GetMapping(value = "/comments/{lesson-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<CommentDto>>> getComments(
            @PathVariable("lesson-id") String lessonId,
            @RequestParam(name = "page") Integer page){
        List<CommentDto> comments = commentService.getComments(lessonId, UserInfoUtil.getUserID(), page);
        int totalItems = commentMapper.countDirectComments(lessonId);
        ResponseBodyDto response = new ResponseBodyDto<>(comments, ResponseCodeEnum.R_200, "OK", totalItems);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/comments", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<CommentDto>> createComment(
            @RequestBody @Valid CommentCreateRequestBody requestBody){
        CommentDto commentDto = commentService.createComment(UserInfoUtil.getUserID(), requestBody);
        ResponseBodyDto response = new ResponseBodyDto<>(commentDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/comments/{comment-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<CommentDto>> editComment(
            @PathVariable("comment-id") String commentId,
            @RequestBody @Valid CommentUpdateRequestBody requestBody){
        CommentDto commentDto = commentService.editComment(commentId, requestBody);
        ResponseBodyDto response = new ResponseBodyDto<>(commentDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/comments/{comment-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<CommentDto>> deleteComment(
            @PathVariable("comment-id") String commentId){
        commentService.deleteComment(commentId);
        ResponseBodyDto response = new ResponseBodyDto<>("Xóa thành công", ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

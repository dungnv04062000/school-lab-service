package com.schoollab.controller.api.v1;

import com.schoollab.common.constants.PageConstant;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.common.util.TimeUtil;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.SubmissionCreateRequestBody;
import com.schoollab.controller.request.SubmissionUpdateRequestBody;
import com.schoollab.dto.SubmissionDto;
import com.schoollab.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class SubmissionController {
    @Autowired
    SubmissionService submissionService;

    @GetMapping(value = "/submissions", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<SubmissionDto>>> getAll(
            @RequestParam(name = "class_id", required = false) String classId,
            @RequestParam(name = "semester_id", required = false) String semesterId,
            @RequestParam(name = "lesson_title", required = false) String lessonTitle,
            @RequestParam(name = "student", required = false) String student,
            @RequestParam(name = "create_at_from", required = false) Long createAtFrom,
            @RequestParam(name = "create_at_to", required = false) Long createAtTo,
            @RequestParam(name = "order_by") String orderBy,
            @RequestParam(name = "page") Integer page
    ){
        List<SubmissionDto> submissionDtos = submissionService.getAll(UserInfoUtil.getUserID(), classId, semesterId, lessonTitle, student,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null,
                orderBy,
                page == null ? 1 : page, PageConstant.SUBMISSION_ROW_NUMBER);
        int count = submissionService.countAll(UserInfoUtil.getUserID(), classId, semesterId, lessonTitle, student,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null);

        ResponseBodyDto response = new ResponseBodyDto(submissionDtos, ResponseCodeEnum.R_200, "OK", count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/submissions/owner", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<SubmissionDto>>> getOwnerSubmissions(
            @RequestParam(name = "semester_id", required = false) String semesterId,
            @RequestParam(name = "lesson_title", required = false) String lessonTitle,
            @RequestParam(name = "create_at_from", required = false) Long createAtFrom,
            @RequestParam(name = "create_at_to", required = false) Long createAtTo,
            @RequestParam(name = "order_by") String orderBy,
            @RequestParam(name = "page") Integer page
    ){
        List<SubmissionDto> submissionDtos = submissionService.getOwnerSubmissions(UserInfoUtil.getUserID(), semesterId, lessonTitle,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null,
                orderBy,
                page, PageConstant.SUBMISSION_ROW_NUMBER);
        int count = submissionService.countOwnerSubmissions(UserInfoUtil.getUserID(), semesterId, lessonTitle,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null);

        ResponseBodyDto response = new ResponseBodyDto(submissionDtos, ResponseCodeEnum.R_200, "OK", count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/submissions/{id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<SubmissionDto>> getOne(
            @PathVariable("id") String submissionId
    ){
        SubmissionDto submissionDto = submissionService.getOne(submissionId);
        ResponseBodyDto response = new ResponseBodyDto(submissionDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/submissions/lessons/{lesson-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<SubmissionDto>> getOneByLessonId(
            @PathVariable("lesson-id") String lessonId
    ){
        SubmissionDto submissionDto = submissionService.getSubmissionByFromIdAndLessonId(UserInfoUtil.getUserID(), lessonId);
        ResponseBodyDto response = new ResponseBodyDto(submissionDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/submissions")
    public ResponseEntity<ResponseBodyDto<SubmissionDto>> createSubmission(
            @ModelAttribute @Valid SubmissionCreateRequestBody requestBody
            ){
        SubmissionDto submissionDto = submissionService.createSubmission(
                UserInfoUtil.getUserID(),
                requestBody.getLessonId(),
                requestBody.getContent(),
                requestBody.getFile());
        ResponseBodyDto response = new ResponseBodyDto(submissionDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/submissions/{submission-id}")
    public ResponseEntity<ResponseBodyDto<SubmissionDto>> editSubmission(
            @PathVariable("submission-id") String submissionId,
            @ModelAttribute @Valid SubmissionUpdateRequestBody requestBody
            ){
        SubmissionDto submissionDto = submissionService.editSubmission(
                submissionId,
                requestBody.getContent(),
                requestBody.getFile());
        ResponseBodyDto response = new ResponseBodyDto(submissionDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

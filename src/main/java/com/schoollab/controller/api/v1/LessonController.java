package com.schoollab.controller.api.v1;

import com.schoollab.common.constants.PageConstant;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.common.util.TimeUtil;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.LessonCreateRequestBody;
import com.schoollab.controller.request.LessonUpdateRequestBody;
import com.schoollab.controller.request.ReOpenLessonRequestBody;
import com.schoollab.dto.LessonDto;
import com.schoollab.model.Class;
import com.schoollab.model.ClassStudent;
import com.schoollab.repository.ClassRepository;
import com.schoollab.repository.ClassStudentRepository;
import com.schoollab.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/v1")
@CrossOrigin("*")
public class LessonController {
    @Autowired
    LessonService lessonService;

    //dành cho giáo viên
    @GetMapping(value = "/lessons", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<LessonDto>>> getAll(
            @RequestParam(name = "class_id") String classId,
            @RequestParam(name = "subject_id", required = false) String subjectId,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "create_at_from", required = false) Long createAtFrom,
            @RequestParam(name = "create_at_to", required = false) Long createAtTo,
            @RequestParam(name = "level_id", required = false) Integer levelId,
            @RequestParam(name = "page") Integer page
            ){
        List<LessonDto> lessonDtos = lessonService.getAllLessons(classId, UserInfoUtil.getUserID(), levelId, subjectId, title,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null,
                page == null ? 1 : page, PageConstant.LESSON_ROW_NUMBER);

        int countLessons = lessonService.countAllLessons(classId, UserInfoUtil.getUserID(), levelId, subjectId, title,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null);
        ResponseBodyDto response = new ResponseBodyDto<>(lessonDtos, ResponseCodeEnum.R_200, "OK", countLessons);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //dành cho học sinh
    @GetMapping(value = "/student-lessons", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<LessonDto>>> getAllForStudent(
            @RequestParam(name = "subject_id") String subjectId,
            @RequestParam(name = "semester_id") String semesterId,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "create_at_from", required = false) Long createAtFrom,
            @RequestParam(name = "create_at_to", required = false) Long createAtTo,
            @RequestParam(name = "level_id", required = false) Integer levelId,
            @RequestParam(name = "page") Integer page
            ){
        List<LessonDto> lessonDtos = lessonService.getAllLessonsForStudent(semesterId, subjectId, title, levelId,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null,
                page, PageConstant.LESSON_ROW_NUMBER);

        int countLessons = lessonService.countAllLessonsForStudent(semesterId, subjectId, title, levelId,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null);
        ResponseBodyDto response = new ResponseBodyDto<>(lessonDtos, ResponseCodeEnum.R_200, "OK", countLessons);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "lessons/{lesson-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<LessonDto>> getOne(@PathVariable("lesson-id") String lessonId){
        LessonDto lessonDto = lessonService.getOne(lessonId);

        ResponseBodyDto responseBodyDto = new ResponseBodyDto(lessonDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(responseBodyDto, HttpStatus.OK);
    }

    @PostMapping(value = "/lessons")
    public ResponseEntity<ResponseBodyDto<LessonDto>> createLesson(
            @RequestParam(value = "file", required = false) MultipartFile multipartFile,
            @ModelAttribute @Valid LessonCreateRequestBody requestBody){
        LessonDto lessonDto = lessonService.createLesson(UserInfoUtil.getUserID(), requestBody, multipartFile);

        ResponseBodyDto response = new ResponseBodyDto<>(lessonDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/lessons/copy-to-other-class/{class-id}/{lesson-id}")
    public ResponseEntity<ResponseBodyDto<LessonDto>> copyLessonToOtherClass(
            @PathVariable("class-id") String classId,
            @PathVariable("lesson-id") String lessonId){
        LessonDto lessonDto = lessonService.copyLessonToOtherClass(lessonId, classId);

        ResponseBodyDto response = new ResponseBodyDto<>(lessonDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/lessons/{lesson-id}")
    public ResponseEntity<ResponseBodyDto<LessonDto>> updateLesson(@PathVariable("lesson-id") String lessonId,
            @RequestParam(value = "file", required = false) MultipartFile multipartFile,
            @ModelAttribute LessonUpdateRequestBody requestBody){
        LessonDto lessonDto = lessonService.updateLesson(lessonId, requestBody, multipartFile);

        ResponseBodyDto response = new ResponseBodyDto<>(lessonDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping(value = "/lessons/finish/{lesson-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<LessonDto>> finishingLesson(@PathVariable("lesson-id") String lessonId){
        LessonDto lessonDto = lessonService.finishingLesson(lessonId);

        ResponseBodyDto response = new ResponseBodyDto<>(lessonDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping(value = "/lessons/re-open/{lesson-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<LessonDto>> reOpenningLesson(@PathVariable("lesson-id") String lessonId,
            @RequestBody @Valid ReOpenLessonRequestBody requestBody){
        LessonDto lessonDto = lessonService.reOpenningLesson(lessonId,
                requestBody.getEndAt() != null ? Instant.ofEpochSecond(requestBody.getEndAt()) : null);

        ResponseBodyDto response = new ResponseBodyDto<>(lessonDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/lessons/{lesson-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<LessonDto>> deleteLesson(@PathVariable("lesson-id") String lessonId){
        LessonDto lessonDto = lessonService.deleteLesson(lessonId);

        ResponseBodyDto response = new ResponseBodyDto<>(lessonDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

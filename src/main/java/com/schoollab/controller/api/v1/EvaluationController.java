package com.schoollab.controller.api.v1;

import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.EvaluationCreateUpdateRequestBody;
import com.schoollab.dto.EvaluationDto;
import com.schoollab.dto.LessonGradeDto;
import com.schoollab.dto.OwnerGradeStatisticDto;
import com.schoollab.dto.SemesterGradeDto;
import com.schoollab.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class EvaluationController {

    @Autowired
    EvaluationService evaluationService;

    @GetMapping(value = "/evaluations/student", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<LessonGradeDto>> getOwnerGrade(
            @RequestParam(value = "lesson_id") String lessonId
            ){
        LessonGradeDto data = evaluationService.getOneGrade(UserInfoUtil.getUserID(), lessonId);

        ResponseBodyDto response = new ResponseBodyDto(data, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/evaluations/owner-grade-statistic", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<OwnerGradeStatisticDto>> getOwnerGradeStatistic(
            @RequestParam(value = "semester_id") String semesterId,
            @RequestParam(value = "subject_id") String subjectId
            ){
        List<OwnerGradeStatisticDto> data = evaluationService.getOwnerGradeStatistic(UserInfoUtil.getUserID(), semesterId, subjectId);

        ResponseBodyDto response = new ResponseBodyDto(data, ResponseCodeEnum.R_200, "OK", data.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/evaluations/template/{lesson-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<EvaluationDto>> evaluateStudentByTemplate(
            @PathVariable("lesson-id") String lessonId,
            @RequestParam("file") MultipartFile multipartFile
            ){
        List<EvaluationDto> evaluationDtos = evaluationService.evaluateStudentByTemplate(
                UserInfoUtil.getUserID(), lessonId, multipartFile);

        ResponseBodyDto response = new ResponseBodyDto(evaluationDtos, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/evaluations/group", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<EvaluationDto>> evaluateGroup(
            @RequestBody @Valid EvaluationCreateUpdateRequestBody requestBody
            ){
        List<EvaluationDto> evaluationDtos = evaluationService.evaluateByGroup(
                UserInfoUtil.getUserID(),
                requestBody.getGroupId(),
                requestBody.getLessonId(),
                requestBody.getGrade());

        ResponseBodyDto response = new ResponseBodyDto(evaluationDtos, ResponseCodeEnum.R_201, "CREATED", evaluationDtos.size());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/evaluations/download-lesson-grades/{lesson-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<String>> downloadTemplate(
            @PathVariable("lesson-id") String lessonId,
            @RequestParam(value = "is_result") Boolean isResult
    ){
        String path = evaluationService.downloadEvaluationTemplate(lessonId, isResult);
        ResponseBodyDto responseBodyDto = new ResponseBodyDto(path, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(responseBodyDto, HttpStatus.OK);
    }

    @GetMapping(value = "/evaluations/download-final-grades/{class-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<String>> downloadClassGrade(
            @PathVariable("class-id") String classId,
            @RequestParam(value = "subject_id") String subjectId
    ){
        String path = evaluationService.downloadClassGrade(classId, subjectId);
        ResponseBodyDto responseBodyDto = new ResponseBodyDto(path, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(responseBodyDto, HttpStatus.OK);
    }

    @GetMapping(value = "/evaluations/final-grades/{class-id}", produces = "application/json")
    public ResponseEntity getClassGrade(
            @PathVariable("class-id") String classId,
            @RequestParam(value = "subject_id") String subjectId
    ){
        List<SemesterGradeDto> data = evaluationService.getClassGrade(classId, subjectId);
        ResponseBodyDto responseBodyDto = new ResponseBodyDto(data, ResponseCodeEnum.R_200, "OK", data.size());
        return new ResponseEntity<>(responseBodyDto, HttpStatus.OK);
    }

    @GetMapping(value = "/evaluations/grades/{lesson-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<LessonGradeDto>>> getGradeList(
            @PathVariable("lesson-id") String lessonId
    ){
        List<LessonGradeDto> data = evaluationService.getEvaluationGradeList(lessonId);
        ResponseBodyDto responseBodyDto = new ResponseBodyDto(data, ResponseCodeEnum.R_200, "OK", data.size());
        return new ResponseEntity<>(responseBodyDto, HttpStatus.OK);
    }

}

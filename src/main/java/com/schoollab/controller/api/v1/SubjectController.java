package com.schoollab.controller.api.v1;

import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.SubjectAddRequest;
import com.schoollab.dto.SubjectDto;
import com.schoollab.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
@CrossOrigin("*")
public class SubjectController {
    @Autowired
    SubjectService subjectService;

    @PostMapping(value = "/subjects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<SubjectDto>> saveSubject(@RequestBody @Valid SubjectAddRequest requestBody){
        SubjectDto subjectDto = subjectService.saveSubject(requestBody);

        ResponseBodyDto response = new ResponseBodyDto(subjectDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/subjects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<SubjectDto>> getSubject() {
        List<SubjectDto> subjects = subjectService.getAllSubject();
        ResponseBodyDto response = new ResponseBodyDto(subjects, ResponseCodeEnum.R_200, "SEARCH", subjects.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/student-subjects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<SubjectDto>> getSubjectsForStudent(
            @RequestParam(name = "subject_name", required = false) String subjectName
    ){
        List<SubjectDto> listSubject = subjectService.filterSubject(UserInfoUtil.getUserID(), subjectName);

        ResponseBodyDto response = new ResponseBodyDto(listSubject, ResponseCodeEnum.R_200, "SEARCH", listSubject.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/subjects/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<SubjectDto>> getOneSubject(@PathVariable("id") Integer subjectId){
        SubjectDto subject = subjectService.getOneSubject(subjectId);

        ResponseBodyDto response = new ResponseBodyDto(subject, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/subjects/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<SubjectDto>> deleteSubject(@PathVariable(name = "id") int subjectId){
        String message = subjectService.deleteSubject(subjectId);

        ResponseBodyDto response = new ResponseBodyDto(message, ResponseCodeEnum.R_200, "DELETE");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

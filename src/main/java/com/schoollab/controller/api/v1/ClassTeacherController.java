package com.schoollab.controller.api.v1;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.constants.PageConstant;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.controller.request.ClassTeacherRequest;
import com.schoollab.dto.ClassTeacherDto;
import com.schoollab.dto.StudentInClassDto;
import com.schoollab.dto.TeacherInClassDto;
import com.schoollab.service.ClassTeacherService;
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
public class ClassTeacherController {
    @Autowired
    ClassTeacherService classTeacherService;

    @PostMapping(value = "/class-teacher", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<ClassTeacherDto>> addTeacherToClass(@RequestBody @Valid ClassTeacherRequest req){
        ClassTeacherDto classTeacherDto = classTeacherService.addTeacherToClass(req);

        ResponseBodyDto response = new ResponseBodyDto(classTeacherDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/class-teacher/{class_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<TeacherInClassDto>> getAllTeacherInClass(
            @PathVariable(value = "class_id") String classId,
            @RequestParam(name = "teacher", required = false) String teacher){
        List<TeacherInClassDto> listTeacher = classTeacherService.filterTeacherInClass(classId, teacher);

        ResponseBodyDto response = new ResponseBodyDto(listTeacher, ResponseCodeEnum.R_200, "OK", listTeacher.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/class-teacher/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<String>> removeTeacherInClass(@PathVariable @Valid String id){
        String message = classTeacherService.removeTeacherInClass(id);

        ResponseBodyDto response = new ResponseBodyDto(message, ResponseCodeEnum.R_200, "DELETE");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //SEARCH ALL TEACHER IN CAMPUS
    @GetMapping(value = "/campus-teachers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<TeacherInClassDto>> getAllTeacherInCampus(
            @RequestParam(value = "campus_id") String campusId,
            @RequestParam(value = "semester_id") String semesterId,
            @RequestParam(name = "class_name", required = false) String className,
            @RequestParam(name = "teacher", required = false) String teacher,
            @RequestParam(name = "gender", required = false) String gender,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer rowNumber){

        List<TeacherInClassDto> listTeacher = classTeacherService.filterTeacherInCampus(campusId, semesterId, Constants.ROLE_TEACHER_ID, className, teacher, gender,
                page == null ? 1 : page, rowNumber == null ? PageConstant.TEACHER_ROW_NUMBER : rowNumber);

        int countAllTeacher = classTeacherService.countAllTeacherInCampus(campusId, semesterId, Constants.ROLE_TEACHER_ID, className, teacher, gender);

        ResponseBodyDto response = new ResponseBodyDto(listTeacher, ResponseCodeEnum.R_200, "SEARCH", countAllTeacher);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //SEARCH ALL TEACHER IN CAMPUS
    @GetMapping(value = "/root-admin/campus-teachers/{campus-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<TeacherInClassDto>> getAllTeacherInCampusForAdmin(
            @PathVariable("campus-id") String campusId,
            @RequestParam(name = "teacher", required = false) String teacher,
            @RequestParam(name = "gender", required = false) String gender,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer rowNumber){

        List<TeacherInClassDto> listTeacher = classTeacherService.getAllTeachers(campusId, teacher, gender,
                page == null ? 1 : page, rowNumber == null ? PageConstant.TEACHER_ROW_NUMBER : rowNumber);

        int countAllTeacher = classTeacherService.countAllTeachers(campusId, teacher, gender);

        ResponseBodyDto response = new ResponseBodyDto(listTeacher, ResponseCodeEnum.R_200, "SEARCH", countAllTeacher);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

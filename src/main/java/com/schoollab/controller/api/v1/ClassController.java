package com.schoollab.controller.api.v1;

import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.ClassCreateRequestBody;
import com.schoollab.controller.request.ClassUpdateRequestBody;
import com.schoollab.dto.ClassDto;
import com.schoollab.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class ClassController {
    @Autowired
    ClassService classService;


    //lấy danh sách lớp học cho admin school
    @GetMapping(value = "/classes/admin-schools", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<ClassDto>>> getClassesForAdmin(
            @RequestParam(name = "campus_id") String campusId,
            @RequestParam(name = "semester_id") String semesterId,
            @RequestParam(name = "grade_id", required = false) String gradeId,
            @RequestParam(name = "class_name", required = false) String className,
            @RequestParam(name = "form_teacher", required = false) String formTeacher){
        List<ClassDto> classDtos = classService.getAllForAdminSchool(campusId, semesterId, gradeId, className, formTeacher);

        ResponseBodyDto response = new ResponseBodyDto<>(classDtos, ResponseCodeEnum.R_200, "OK", classDtos.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //lấy danh sách lớp học cho giáo viên bộ môn
    @GetMapping(value = "/classes/teacher", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<ClassDto>>> getClassesForSubjectTeacher(
            @RequestParam(name = "semester_id") String semesterId,
            @RequestParam(name = "grade_id", required = false) String gradeId,
            @RequestParam(name = "form_teacher", required = false) String formTeacher,
            @RequestParam(name = "class_name", required = false) String className){
        List<ClassDto> classDtos = classService.getAll(UserInfoUtil.getUserID(), semesterId, gradeId, className, formTeacher);

        ResponseBodyDto response = new ResponseBodyDto<>(classDtos, ResponseCodeEnum.R_200, "OK", classDtos.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/classes/{class-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<ClassDto>> getOne(@PathVariable(value = "class-id") String classId){
        ClassDto classDto = classService.getOne(classId);

        ResponseBodyDto response = new ResponseBodyDto<>(classDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/classes", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<ClassDto>> createClass(
            @RequestBody @Valid ClassCreateRequestBody requestBody){
        ClassDto classDto = classService.createClass(requestBody);

        ResponseBodyDto response = new ResponseBodyDto<>(classDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/classes/{class-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<ClassDto>> updateClass(@PathVariable(value = "class-id") String classId,
            @RequestBody @Valid ClassUpdateRequestBody requestBody){
        ClassDto classDto = classService.updateClass(classId, requestBody);

        ResponseBodyDto response = new ResponseBodyDto<>(classDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/classes/{class-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<ClassDto>> deleteClass(@PathVariable(value = "class-id") String classId){
        classService.deleteClass(classId);

        ResponseBodyDto response = new ResponseBodyDto<>("DELETED", ResponseCodeEnum.R_200, "DELETED");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

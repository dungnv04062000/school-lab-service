package com.schoollab.controller.api.v1;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.constants.PageConstant;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.controller.request.ClassManyStudentRequest;
import com.schoollab.controller.request.ClassStudentAddRequestBody;
import com.schoollab.dto.ClassStudentDto;
import com.schoollab.dto.StudentInClassDto;
import com.schoollab.service.ClassStudentService;
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
public class ClassStudentController {
    @Autowired
    ClassStudentService classStudentService;

    @PostMapping(value = "/class-student/save-one", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<ClassStudentDto>> addSingleStudentToClass(@RequestBody @Valid ClassStudentAddRequestBody req){
        ClassStudentDto classStudentDto = classStudentService.saveStudentToClass(req);

        ResponseBodyDto response = new ResponseBodyDto(classStudentDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/class-student/save-many")
    public ResponseEntity<ResponseBodyDto<ClassStudentDto>> addManyStudentToClass(
            @ModelAttribute @Valid ClassManyStudentRequest req){
        List<ClassStudentDto> listClassStudentDto = classStudentService.saveManyStudentToClass(req);

        ResponseBodyDto response = new ResponseBodyDto(listClassStudentDto, ResponseCodeEnum.R_201, "CREATED", listClassStudentDto.size());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/class-student/{class_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<StudentInClassDto>> getAllStudentInClass(
            @PathVariable(value = "class_id") String classId,
            @RequestParam(name = "student", required = false) String student,
            @RequestParam(name = "gender", required = false) String gender){
        List<StudentInClassDto> listStudent = classStudentService.filterStudent(classId, student, gender);

        ResponseBodyDto response = new ResponseBodyDto(listStudent, ResponseCodeEnum.R_200, "OK", listStudent.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/class-student/{id}", produces = "application/json")
    public ResponseEntity<String> removeStudent(
            @PathVariable("id") String classStudentId
    ){
        classStudentService.removeStudent(classStudentId);
        return new ResponseEntity<>("Xóa thành công", HttpStatus.OK);
    }

    //SEARCH ALL STUDENT IN CAMPUS
    @GetMapping(value = "/campus-students", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<StudentInClassDto>> getAllStudentInCampus(
            @RequestParam(value = "campus_id") String campusId,
            @RequestParam(value = "semester_id") String semesterId,
            @RequestParam(name = "grade_id", required = false) Integer gradeId,
            @RequestParam(name = "class_name", required = false) String className,
            @RequestParam(name = "student", required = false) String student,
            @RequestParam(name = "gender", required = false) String gender,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "size", required = false) Integer rowNumber){

        List<StudentInClassDto> listStudent = classStudentService.filterStudentInCampus(campusId, semesterId, gradeId, Constants.ROLE_STUDENT_ID, className, student, gender,
                page == null ? 1 : page, rowNumber == null ? PageConstant.STUDENT_ROW_NUMBER : rowNumber);

        int countAllStudent = classStudentService.countAllStudentInCampus(campusId, semesterId, gradeId, Constants.ROLE_STUDENT_ID, className, student, gender);

        ResponseBodyDto response = new ResponseBodyDto(listStudent, ResponseCodeEnum.R_200, "SEARCH", countAllStudent);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //SEARCH ALL STUDENT IN CAMPUS
    @GetMapping(value = "/root-admin/campus-students/{campus-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<StudentInClassDto>> getAllStudentInCampusForRootAdmin(
            @PathVariable("campus-id") String campusId,
            @RequestParam(name = "student", required = false) String student,
            @RequestParam(name = "gender", required = false) String gender,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "size", required = false) Integer rowNumber){

        List<StudentInClassDto> listStudent = classStudentService.searchAllStudent(campusId, student, gender,
                page == null ? 1 : page, rowNumber == null ? PageConstant.STUDENT_ROW_NUMBER : rowNumber);

        int countAllStudent = classStudentService.countAllStudent(campusId, student, gender);

        ResponseBodyDto response = new ResponseBodyDto(listStudent, ResponseCodeEnum.R_200, "SEARCH", countAllStudent);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

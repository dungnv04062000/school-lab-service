package com.schoollab.controller.api.v1;

import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.CampusCreateRequestBody;
import com.schoollab.controller.request.SemesterCreateRequestBody;
import com.schoollab.controller.request.SemesterUpdateRequestBody;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.dto.CampusDto;
import com.schoollab.dto.SemesterDto;
import com.schoollab.service.CampusService;
import com.schoollab.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class SemesterController {

    @Autowired
    SemesterService semesterService;

    @PostMapping(value = "/semesters", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<SemesterDto>> createSemester(@RequestBody @Valid SemesterCreateRequestBody requestBody){
        SemesterDto semesterDto = semesterService.createSemester(requestBody);

        ResponseBodyDto response = new ResponseBodyDto<>(semesterDto, ResponseCodeEnum.R_201, "CREATE");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/semesters/{semester-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<SemesterDto>> updateCampus(@PathVariable(name = "semester-id") String semesterId,
                                                                   @RequestBody @Valid SemesterUpdateRequestBody requestBody){
        SemesterDto semesterDto = semesterService.updateSemester(semesterId, requestBody);

        ResponseBodyDto response = new ResponseBodyDto<>(semesterDto, ResponseCodeEnum.R_200, "UPDATE");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/semesters/{semester-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<String>> deleteSemester(@PathVariable(name = "semester-id") String semesterId){
        semesterService.deleteSemester(semesterId);

        ResponseBodyDto response = new ResponseBodyDto<>("Xóa học kỳ thành công", ResponseCodeEnum.R_200, "DELETE");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/semesters", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<SemesterDto>> getSemesters(
            @RequestParam(name = "name", required = false) String semesterName,
            @RequestParam(name = "year", required = false) Integer year
    ){
        List<SemesterDto> semesterDtos = semesterService.getSemesters(UserInfoUtil.getUserID(), semesterName, year);

        ResponseBodyDto response = new ResponseBodyDto<>(semesterDtos, ResponseCodeEnum.R_200, "OK", semesterDtos.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

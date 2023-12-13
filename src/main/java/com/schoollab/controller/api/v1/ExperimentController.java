package com.schoollab.controller.api.v1;

import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.ExperimentCreateRequestBody;
import com.schoollab.controller.request.ExperimentUpdateRequestBody;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.dto.ExperimentDto;
import com.schoollab.service.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class ExperimentController {

    @Autowired
    ExperimentService experimentService;

    @PostMapping(value = "/experiments", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<ExperimentDto>> createExperiment(
            @RequestBody @Valid ExperimentCreateRequestBody requestBody
            ){
        ExperimentDto experimentDto = experimentService.createExperiment(UserInfoUtil.getUserID(), requestBody);

        ResponseBodyDto response = new ResponseBodyDto(experimentDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/experiments/{experiment-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<ExperimentDto>> updateExperiment(
            @PathVariable("experiment-id")String experimentId,
            @RequestBody @Valid ExperimentUpdateRequestBody requestBody
            ){
        ExperimentDto experimentDto = experimentService.updateExperiment(experimentId, requestBody);

        ResponseBodyDto response = new ResponseBodyDto(experimentDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/experiments", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<ExperimentDto>> getExperiments(
            @RequestParam("student_id") String studentId,
            @RequestParam("lesson_id") String lessonId
    ){
        List<ExperimentDto> data = experimentService.getExperiments(studentId, lessonId);
        ResponseBodyDto response = new ResponseBodyDto(data, ResponseCodeEnum.R_200, "OK", data.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

package com.schoollab.controller.api.v1;

import com.schoollab.controller.request.ExperimentDetailCreateRequestBody;
import com.schoollab.controller.request.ExperimentDetailUpdateRequestBody;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.dto.ExperimentDetailDto;
import com.schoollab.dto.ExperimentDto;
import com.schoollab.service.ExperimentDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/experiments")
public class ExperimentDetailController {

    @Autowired
    ExperimentDetailService experimentDetailService;

    @PostMapping(value = "/details", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<ExperimentDto>> createExperimentDetail(
            @RequestBody @Valid ExperimentDetailCreateRequestBody requestBody
            ){
        ExperimentDetailDto experimentDto = experimentDetailService.createExperimentDetail(requestBody);

        ResponseBodyDto response = new ResponseBodyDto(experimentDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/details/{id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<ExperimentDto>> updateExperimentDetail(
            @PathVariable("id")String id,
            @RequestBody @Valid ExperimentDetailUpdateRequestBody requestBody
            ){
        ExperimentDetailDto experimentDto = experimentDetailService.updateExperimentDetail(id, requestBody);

        ResponseBodyDto response = new ResponseBodyDto(experimentDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

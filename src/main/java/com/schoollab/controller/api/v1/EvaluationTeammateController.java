package com.schoollab.controller.api.v1;

import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.EvaluationTeammateCreateUpdateRequestBody;
import com.schoollab.dto.EvaluationTeammateDto;
import com.schoollab.service.EvaluationTeammateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/evaluations")
public class EvaluationTeammateController {

    @Autowired
    EvaluationTeammateService evaluationTeammateService;

    @PostMapping(value = "/teammates", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<EvaluationTeammateDto>> evaluateTeammate(
            @RequestBody @Valid EvaluationTeammateCreateUpdateRequestBody requestBody
            ){
        List<EvaluationTeammateDto> evaluationTeammateDtos = evaluationTeammateService.evaluateTeammates(
                UserInfoUtil.getUserID(),
                requestBody);

        ResponseBodyDto response = new ResponseBodyDto(evaluationTeammateDtos,
                ResponseCodeEnum.R_201,
                "CREATED",
                evaluationTeammateDtos.size());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/teammates", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<EvaluationTeammateDto>>> getAllEvaluationTeammates(
            @RequestParam(name = "lesson_id") String lessonId
    ){
        List<EvaluationTeammateDto> data = evaluationTeammateService.getAllEvaluationTeammates(lessonId, UserInfoUtil.getUserID());
        ResponseBodyDto response = new ResponseBodyDto(data,
                ResponseCodeEnum.R_200,
                "OK",
                data.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

package com.schoollab.controller.api.v1;

import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.EvaluationGroupCreateUpdateRequestBody;
import com.schoollab.dto.EvaluationGroupDto;
import com.schoollab.service.EvaluationGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/evaluations")
public class EvaluationGroupController {

    @Autowired
    EvaluationGroupService evaluationGroupService;

    @PostMapping(value = "/groups", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<EvaluationGroupDto>> evaluateGroup(
            @RequestBody @Valid EvaluationGroupCreateUpdateRequestBody requestBody
            ){
        EvaluationGroupDto evaluationGroupDto = evaluationGroupService.evaluateGroup(
                UserInfoUtil.getUserID(),
                requestBody.getGroupId(),
                requestBody.getLessonId(),
                requestBody.getPreparation(),
                requestBody.getImplementation(),
                requestBody.getPresentation(),
                requestBody.getProduction());

        ResponseBodyDto response = new ResponseBodyDto(evaluationGroupDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/groups", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<EvaluationGroupDto>>> getAllEvaluationGroups(
            @RequestParam(name = "lesson_id") String lessonId
    ){
        List<EvaluationGroupDto> data = evaluationGroupService.getAllEvaluationGroups(lessonId, UserInfoUtil.getUserID());
        ResponseBodyDto response = new ResponseBodyDto(data,
                ResponseCodeEnum.R_200,
                "OK",
                data.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

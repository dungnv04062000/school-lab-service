package com.schoollab.controller.api.v1;

import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.ClassGroupCreateRequestBody;
import com.schoollab.dto.ClassGroupDto;
import com.schoollab.service.ClassGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class ClassGroupController {

    @Autowired
    ClassGroupService classGroupService;

    @GetMapping(value = "/class-groups/{class-id}/{lesson-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<ClassGroupDto>>> getClassGroups(
            @PathVariable("class-id") String classId,
            @PathVariable("lesson-id") String lessonId
    ){
        List<ClassGroupDto> classGroupDtos = classGroupService.getAllGroups(classId, lessonId);
        ResponseBodyDto response = new ResponseBodyDto(classGroupDtos, ResponseCodeEnum.R_200, "OK", classGroupDtos.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/class-groups/owner/{lesson-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<ClassGroupDto>> getOwnerGroups(
            @PathVariable("lesson-id") String lessonId
    ){
        ClassGroupDto classGroupDtos = classGroupService.getOwnerGroups(lessonId, UserInfoUtil.getUserID());
        ResponseBodyDto response = new ResponseBodyDto(classGroupDtos, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/class-groups/template/{class-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<String>> downloadTemplate(
            @PathVariable("class-id") String classId
    ){
        String path = classGroupService.createGroupTemplate(classId);
        ResponseBodyDto responseBodyDto = new ResponseBodyDto(path, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(responseBodyDto, HttpStatus.OK);
    }

    @PostMapping(value = "/class-groups/random", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<ClassGroupDto>>> randomGroups(
            @RequestBody @Valid ClassGroupCreateRequestBody requestBody
            ){
        List<ClassGroupDto> classGroupDtos = classGroupService.randomGroup(
                requestBody.getClassId(),
                requestBody.getLessonId(),
                requestBody.getNumberOfGroup(),
                requestBody.getIsOverride());
        ResponseBodyDto response = new ResponseBodyDto(classGroupDtos, ResponseCodeEnum.R_201, "CREATED", classGroupDtos.size());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/class-groups/order", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<ClassGroupDto>>> createGroups(
            @RequestBody @Valid ClassGroupCreateRequestBody requestBody
            ){
        List<ClassGroupDto> classGroupDtos = classGroupService.createGroupByOrder(
                requestBody.getClassId(),
                requestBody.getLessonId(),
                requestBody.getNumberOfGroup(),
                requestBody.getIsOverride());
        ResponseBodyDto response = new ResponseBodyDto(classGroupDtos, ResponseCodeEnum.R_201, "CREATED", classGroupDtos.size());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/class-groups/template/{class-id}/{lesson-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<List<ClassGroupDto>>> createGroups(
            @PathVariable("class-id") String classId,
            @PathVariable("lesson-id") String lessonId,
            @RequestParam("file") MultipartFile multipartFile
            ){
        List<ClassGroupDto> classGroupDtos = classGroupService.createGroupByTemplate(classId, lessonId, multipartFile);
        ResponseBodyDto response = new ResponseBodyDto(classGroupDtos, ResponseCodeEnum.R_201, "CREATED", classGroupDtos.size());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}

package com.schoollab.controller.api.v1;

import com.schoollab.common.constants.PageConstant;
import com.schoollab.common.util.TimeUtil;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.SupportUpdateRequestBody;
import com.schoollab.controller.request.SupportUserRequest;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.dto.SupportUserDto;
import com.schoollab.service.SupportUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/v1")
@CrossOrigin("*")
public class SupportController {
    @Autowired
    SupportUserService supportUserService;

    @PostMapping(value = "/supports", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<SupportUserDto>> createSupport(@RequestBody @Valid SupportUserRequest supportUserRequest){
        SupportUserDto supportUserDto = supportUserService.save(supportUserRequest);

        ResponseBodyDto response = new ResponseBodyDto(supportUserDto, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/supports/{support-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<SupportUserDto>> updateSupport(
            @PathVariable("support-id")String supportId,
            @RequestBody SupportUpdateRequestBody requestBody){
        SupportUserDto supportUserDto = supportUserService.updateStatus(supportId, requestBody.getResponse());

        ResponseBodyDto response = new ResponseBodyDto(supportUserDto, ResponseCodeEnum.R_200, "UPDATE");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/supports", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchSupports(@RequestParam(name = "priority", required = false) String priority,
                                 @RequestParam(name = "status", required = false) String status,
                                 @RequestParam(name = "create_at_from", required = false) Long createAtFrom,
                                 @RequestParam(name = "create_at_to", required = false) Long createAtTo,
                                 @RequestParam(name = "type_sort") String type,
                                 @RequestParam(name = "page") Integer page
                                         ){
        List<SupportUserDto> list = supportUserService
                .filterSupportUser(priority, status,
                        createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                        createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null, null,
                        type, page, PageConstant.SUPPORT_ROW_NUMBER);
        Integer totalItems = supportUserService.countSupportUser(priority, status,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null, null);

        ResponseBodyDto response = new ResponseBodyDto(list, ResponseCodeEnum.R_200, "SEARCH", totalItems);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/supports/owner", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getOwnerSupports(@RequestParam(name = "priority", required = false) String priority,
                                 @RequestParam(name = "status", required = false) String status,
                                 @RequestParam(name = "create_at_from", required = false) Long createAtFrom,
                                 @RequestParam(name = "create_at_to", required = false) Long createAtTo,
                                 @RequestParam(name = "type_sort") String type,
                                 @RequestParam(name = "page") Integer page
                                 ){
        List<SupportUserDto> list = supportUserService
                .filterSupportUser(priority, status,
                        createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                        createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null,
                        UserInfoUtil.getUserID(),
                        type, page, PageConstant.SUPPORT_ROW_NUMBER);
        Integer totalItems = supportUserService.countSupportUser(priority, status,
                createAtFrom != null ? TimeUtil.getStartOfThisDay(Instant.ofEpochSecond(createAtFrom)) : null,
                createAtTo != null ? TimeUtil.getEndOfThisDay(Instant.ofEpochSecond(createAtTo)) : null, UserInfoUtil.getUserID());

        ResponseBodyDto response = new ResponseBodyDto(list, ResponseCodeEnum.R_200, "SEARCH", totalItems);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/supports/{support-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchSupports(@PathVariable("support-id") String supportId){
        SupportUserDto support = supportUserService.getOne(supportId);

        ResponseBodyDto response = new ResponseBodyDto(support, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

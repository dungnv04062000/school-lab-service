package com.schoollab.controller.api.v1;

import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.controller.request.UserRoleCreateRequestBody;
import com.schoollab.model.UserRole;
import com.schoollab.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
public class UserRoleController {

    @Autowired
    UserRoleService userRoleService;

    @PostMapping(value = "/user-roles", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<UserRole>> addUserRole(@RequestBody @Valid UserRoleCreateRequestBody requestBody){
        UserRole userRole = userRoleService.addUserRole(requestBody.getUserId(), requestBody.getRole());

        ResponseBodyDto response = new ResponseBodyDto<>(userRole, ResponseCodeEnum.R_201, "CREATED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

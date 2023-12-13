package com.schoollab.controller.api.v1;

import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.EmailRegisterRequestBody;
import com.schoollab.controller.request.UserRegisterRequestBody;
import com.schoollab.controller.request.UserUpdateRequestBody;
import com.schoollab.dto.AccountDto;
import com.schoollab.dto.UserDto;

import com.schoollab.repository.UserRepository;
import com.schoollab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
@CrossOrigin("*")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @GetMapping(value = "/users/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<UserDto>> getUsers(){
        System.out.println("userId: " + UserInfoUtil.getUserID());
        UserDto userDto = userService.getUser(UserInfoUtil.getUserID());
        return new ResponseEntity<>(new ResponseBodyDto<>(userDto, ResponseCodeEnum.R_200, "OK"), HttpStatus.OK);
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<UserDto>> register(@RequestBody @Valid UserRegisterRequestBody requestBody){
        UserDto userDto = userService.register(requestBody);

        ResponseBodyDto response = new ResponseBodyDto(userDto, ResponseCodeEnum.R_201, "CREATED");
        return  new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/register-by-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<UserDto>> registerByEmail(@RequestBody @Valid EmailRegisterRequestBody requestBody){
        UserDto userDto = userService.registerByGmail(requestBody);

        ResponseBodyDto response = new ResponseBodyDto(userDto, ResponseCodeEnum.R_201, "CREATED");
        return  new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/users/{campus-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<UserDto>> getUserByCampusId(
            @PathVariable(name = "campus-id") String campusId){
        List<UserDto> data = userService.getUserByCampusId(campusId);
        ResponseBodyDto response = new ResponseBodyDto(data, ResponseCodeEnum.R_200, "OK", data.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<AccountDto>> updateUser(@RequestBody @Valid UserUpdateRequestBody requestBody){
        UserDto userDto = userService.updateUser(UserInfoUtil.getUserID(), requestBody);
        ResponseBodyDto response = new ResponseBodyDto(userDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/users/change-avatar")
    public ResponseEntity<ResponseBodyDto<AccountDto>> changeAvatar(@RequestParam("file") MultipartFile multipartFile){
        UserDto userDto = userService.changeAvatar(UserInfoUtil.getUserID(), multipartFile);
        ResponseBodyDto response = new ResponseBodyDto(userDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/users/set-default-avatar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseBodyDto<AccountDto>> defaultAvatar(){
        UserDto userDto = userService.setDefaultAvatar(UserInfoUtil.getUserID());
        ResponseBodyDto response = new ResponseBodyDto(userDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

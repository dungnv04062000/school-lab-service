package com.schoollab.service;

import com.schoollab.controller.request.EmailRegisterRequestBody;
import com.schoollab.controller.request.UserRegisterRequestBody;
import com.schoollab.controller.request.UserUpdateRequestBody;
import com.schoollab.dto.AccountDto;
import com.schoollab.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    UserDto registerByGmail(EmailRegisterRequestBody req);

    UserDto register(UserRegisterRequestBody req);

    UserDto updateUser(String userId, UserUpdateRequestBody requestBody);

    UserDto changeAvatar(String userId, MultipartFile multipartFile);

    UserDto setDefaultAvatar(String userId);

    List<UserDto> getUserByCampusId(String campusId);

    UserDto getUser(String userId);

    String generateSalt();

    String hasingPassword(String password, String salt);
}

package com.schoollab.service;

import com.schoollab.controller.request.ForgotPasswordRequestBody;
import com.schoollab.controller.request.ResetPasswordRequestBody;
import com.schoollab.dto.AccountDto;

public interface AccountService {

    AccountDto changePassword(String userId, String currentPassword, String newPassword);

    String forgotPassword(ForgotPasswordRequestBody requestBody);

    AccountDto resetPassword(String userId, ResetPasswordRequestBody requestBody);

    AccountDto verifyAccount(String userId);
}

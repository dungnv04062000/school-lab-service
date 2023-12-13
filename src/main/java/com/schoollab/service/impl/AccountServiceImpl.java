package com.schoollab.service.impl;

import com.schoollab.common.error.BadRequestException;
import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.error.UnKnownException;
import com.schoollab.controller.request.ForgotPasswordRequestBody;
import com.schoollab.controller.request.ResetPasswordRequestBody;
import com.schoollab.controller.request.SendMailRequestBody;
import com.schoollab.dto.AccountDto;
import com.schoollab.dto.SendMailResponseDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.model.Account;
import com.schoollab.model.User;
import com.schoollab.repository.AccountRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.service.AccountService;
import com.schoollab.service.SendMailService;
import com.schoollab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    GenericMapper genericMapper;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    SendMailService sendMailService;

    @Override
    public AccountDto changePassword(String userId, String currentPassword, String newPassword) {
        Account account = accountRepository.findByUserId(userId);
        if(account == null){
            throw new NotFoundException("Không tìm thấy tài khoản này");
        }
        String currentSalt = account.getSalt();
        if (!passwordEncoder.matches(currentPassword + currentSalt, account.getPassword())) {
            throw new BadRequestException("Mật khẩu chưa đúng, vui lòng nhập lại");
        }

        String newSalt = userService.generateSalt();
        String newHashedPassword = userService.hasingPassword(newPassword, newSalt);
        account.setPassword(newHashedPassword);
        account.setSalt(newSalt);
        Account dbAccount = accountRepository.save(account);
        return genericMapper.mapToTypeNotNullProperty(dbAccount, AccountDto.class);
    }

    @Override
    public String forgotPassword(ForgotPasswordRequestBody requestBody) {
        User user = userRepository.findByEmail(requestBody.getEmail());
        if(user == null){
            throw new NotFoundException("Email chưa được đăng ký");
        }
        Account account = accountRepository.findByUserId(user.getId());
        if(account == null){
            throw new NotFoundException("Người dùng chưa tạo tài khoản");
        }
        if(!account.getIsVerify()){
            throw new BadRequestException("Tài khoản của bạn chưa được xác nhận, vui lòng kiểm tra email đăng ký");
        }

        try {
            Map<String, Object> model = new HashMap<>();
            model.put("name", user.getFirstName()+ " " + user.getLastName());
            model.put("userId", user.getId());
            SendMailRequestBody sendMailRequestBody = new SendMailRequestBody()
                    .setSubject("Reset Password - SchoolLab")
                    .setTo(user.getEmail())
                    .setType("forgot_password");
            SendMailResponseDto message = sendMailService.sendMail(sendMailRequestBody, model);
        }catch (Exception e){
            throw new UnKnownException("Có lỗi trong quá trình xử lý");
        }
        return "Gửi mail thành công.";
    }

    @Override
    public AccountDto resetPassword(String userId, ResetPasswordRequestBody requestBody) {
        Account account = accountRepository.findByUserId(userId);
        if(account == null){
            throw new NotFoundException("Không tìm thấy tài khoản này");
        }

        String newSalt = userService.generateSalt();
        String newHashedPassword = userService.hasingPassword(requestBody.getPassword(), newSalt);
        account.setPassword(newHashedPassword);
        account.setSalt(newSalt);
        account.setUpdateAt(Instant.now());
        Account dbAccount = accountRepository.save(account);
        return genericMapper.mapToTypeNotNullProperty(dbAccount, AccountDto.class);
    }

    @Override
    public AccountDto verifyAccount(String userId) {
        Account account = accountRepository.findByUserId(userId);
        if(account == null){
            throw new NotFoundException("Không tìm thấy tài khoản này");
        }

        if(account.getIsVerify()){
            throw new BadRequestException("Tài khoản này đã được xác thực!");
        }

        account.setIsVerify(true);
        accountRepository.save(account);
        return genericMapper.mapToTypeNotNullProperty(account, AccountDto.class);
    }
}

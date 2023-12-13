package com.schoollab.service.impl;

import com.schoollab.common.error.NotFoundException;
import com.schoollab.common.error.UnKnownException;
import com.schoollab.controller.request.ForgotPasswordRequestBody;
import com.schoollab.controller.request.ResetPasswordRequestBody;
import com.schoollab.dto.AccountDto;
import com.schoollab.dto.mapper.GenericMapper;
import com.schoollab.model.Account;
import com.schoollab.model.User;
import com.schoollab.modelTest.SetUpModelTest;
import com.schoollab.repository.AccountRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.service.SendMailService;
import com.schoollab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {
    @InjectMocks
    AccountServiceImpl accountService;
    @Mock
    AccountRepository accountRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    GenericMapper genericMapper;
    @Mock
    UserService userService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    SendMailService sendMailService;
    AccountDto accountDto;
    Account account;
    User user;
    ForgotPasswordRequestBody forgotReq;
    ResetPasswordRequestBody resetReq;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        account = SetUpModelTest.setUpAccount();
        accountDto = SetUpModelTest.setUpAccountDto();
        forgotReq = SetUpModelTest.forgotPassReq();
        resetReq = SetUpModelTest.resetReq();
        user = SetUpModelTest.setUpUser();
    }

    @Test
    void changePassword_notFound() {
        when(accountRepository.findByUserId(Mockito.eq("HE140705"))).thenReturn(null);
        AccountDto response = null;
        try {
            response = accountService.changePassword("HE140705", "dung1234", "dungHip");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void changePassword_currentPassFail(){
        when(accountRepository.findByUserId(Mockito.eq("HE140705"))).thenReturn(account);
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(false);
        AccountDto response = null;
        try {
            response = accountService.changePassword("HE140705", "dung1234", "dungHip");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void changePassword_success(){
        when(accountRepository.findByUserId(Mockito.eq("HE140705"))).thenReturn(account);
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);
        when(userService.generateSalt()).thenReturn("private");
        when(userService.hasingPassword(Mockito.any(), Mockito.any())).thenReturn("newPass");
        when(accountRepository.save(Mockito.any())).thenReturn(account);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(AccountDto.class))).thenReturn(accountDto);

        AccountDto response = accountService.changePassword("HE140705", "dung1234", "dungHip");

        assertNotNull(response);
        assertEquals(response, accountDto);
    }

    @Test
    void changePassword_exception(){
        when(accountRepository.findByUserId(Mockito.eq("HE140705"))).thenReturn(account);
        when(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true);
        when(userService.generateSalt()).thenReturn("private");

        AccountDto response = null;
        try {
            when(userService.hasingPassword(Mockito.any(), Mockito.any())).thenThrow(Exception.class);
            response = accountService.changePassword("HE140705", "dung1234", "dungHip");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void verifyAccount_notFound() {
        when(accountRepository.findByUserId(Mockito.eq("HE140705"))).thenReturn(null);
        AccountDto response = null;
        try {
            response = accountService.verifyAccount("HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void verifyAccount_isVerified() {
        when(accountRepository.findByUserId(Mockito.eq("HE140705"))).thenReturn(account);
        AccountDto response = null;
        try {
            response = accountService.verifyAccount("HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void verifyAccount_exception() {
        AccountDto response = null;
        try {
            account.setIsVerify(false);
            when(accountRepository.findByUserId(Mockito.eq("HE140705"))).thenReturn(account);
            when(accountRepository.save(Mockito.any())).thenThrow(Exception.class);
            response = accountService.verifyAccount("HE140705");
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
        }
    }

    @Test
    void verifyAccount_success() {
        account.setIsVerify(false);
        when(accountRepository.findByUserId(Mockito.eq("HE140705"))).thenReturn(account);
        when(accountRepository.save(Mockito.any())).thenReturn(account);
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(AccountDto.class))).thenReturn(accountDto);

        AccountDto response = accountService.verifyAccount("HE140705");
        assertNotNull(response);
        assertEquals(response, accountDto);
    }

    @Test
    void forgotPassword_email_notFound(){
        when(userRepository.findByEmail(Mockito.any())).thenReturn(null);
        String response = null;
        try {
            response = accountService.forgotPassword(forgotReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), "Email chưa được đăng ký");
        }
    }

    @Test
    void forgotPassword_account_notFound(){
        when(userRepository.findByEmail(Mockito.any())).thenReturn(user);
        when(accountRepository.findByUserId(Mockito.any())).thenReturn(null);
        String response = null;
        try {
            response = accountService.forgotPassword(forgotReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), "Người dùng chưa tạo tài khoản");
        }
    }

    @Test
    void forgotPassword_account_isNotVerify(){
        account.setIsVerify(false);
        when(userRepository.findByEmail(Mockito.any())).thenReturn(user);
        when(accountRepository.findByUserId(Mockito.any())).thenReturn(account);
        String response = null;
        try {
            response = accountService.forgotPassword(forgotReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), "Tài khoản của bạn chưa được xác nhận, vui lòng kiểm tra email đăng ký");
        }
    }

    @Test
    void forgotPassword_exception(){
        Exception ex = new UnKnownException("Có lỗi trong quá trình xử lý");
        when(userRepository.findByEmail(Mockito.any())).thenReturn(user);
        when(accountRepository.findByUserId(Mockito.any())).thenReturn(account);
        when(sendMailService.sendMail(Mockito.any(), Mockito.any())).thenThrow(ex);
        String response = null;
        try {
            response = accountService.forgotPassword(forgotReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), ex.getMessage());
        }
    }

    @Test
    void forgotPassword_success(){
        when(userRepository.findByEmail(Mockito.any())).thenReturn(user);
        when(accountRepository.findByUserId(Mockito.any())).thenReturn(account);
        when(sendMailService.sendMail(Mockito.any(), Mockito.any())).thenReturn(null);

        String response = accountService.forgotPassword(forgotReq);
        assertNotNull(response);
        assertEquals("Gửi mail thành công.", response);
    }

    @Test
    void resetPassword_account_notFound(){
        when(accountRepository.findByUserId(Mockito.any())).thenReturn(null);
        AccountDto response = null;
        try {
            response = accountService.resetPassword("HE140705", resetReq);
        }catch (Exception e){
            assertTrue(e instanceof Exception);
            assertNull(response);
            assertEquals(e.getMessage(), "Không tìm thấy tài khoản này");
        }
    }

    @Test
    void resetPassword_success(){
        when(accountRepository.findByUserId(Mockito.any())).thenReturn(account);
        when(userService.generateSalt()).thenReturn("123");
        when(userService.hasingPassword(Mockito.any(), Mockito.any())).thenReturn("abca");
        when(genericMapper.mapToTypeNotNullProperty(Mockito.any(), Mockito.eq(AccountDto.class))).thenReturn(accountDto);

        AccountDto response = accountService.resetPassword("HE140705", resetReq);
        assertNotNull(response);
        assertEquals(accountDto, response);
    }
}
package com.schoollab.controller.api.v1;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoollab.common.constants.Constants;
import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.common.util.UserInfoUtil;
import com.schoollab.controller.request.ChangePasswordRequestBody;
import com.schoollab.controller.request.ForgotPasswordRequestBody;
import com.schoollab.controller.request.ResetPasswordRequestBody;
import com.schoollab.dto.AccountDto;
import com.schoollab.model.Account;
import com.schoollab.repository.AccountRepository;
import com.schoollab.repository.UserRepository;
import com.schoollab.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/v1/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @PostMapping(value = "/verify-accounts/{user-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<AccountDto>> verifyAccount(@PathVariable("user-id") String userId){
        AccountDto accountDto = accountService.verifyAccount(userId);

        ResponseBodyDto response = new ResponseBodyDto(accountDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/change-password", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<AccountDto>> changePassword(@RequestBody @Valid ChangePasswordRequestBody requestBody){
        AccountDto accountDto = accountService.changePassword(UserInfoUtil.getUserID(), requestBody.getCurrentPassword(), requestBody.getNewPassword());

        ResponseBodyDto response = new ResponseBodyDto(accountDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/forgot-password", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<AccountDto>> resetPassword(@RequestBody @Valid ForgotPasswordRequestBody requestBody){
        String responseMail = accountService.forgotPassword(requestBody);

        ResponseBodyDto response = new ResponseBodyDto(responseMail, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping(value = "/reset-password/{user-id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDto<AccountDto>> forgotPassword(@PathVariable("user-id") String userId,
                                                                      @RequestBody @Valid ResetPasswordRequestBody requestBody){
        AccountDto accountDto = accountService.resetPassword(userId, requestBody);

        ResponseBodyDto response = new ResponseBodyDto(accountDto, ResponseCodeEnum.R_200, "OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/refresh-token", produces = "application/json")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith(Constants.JWT_PREFIX)) {
            try {
                String refreshToken = authorizationHeader.substring(Constants.JWT_PREFIX.length());
                Algorithm algorithm = Algorithm.HMAC256(Constants.JWT_SECRET_KEY.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String userId = decodedJWT.getSubject();
                Account account = accountRepository.findByUserId(userId);
                String accessToken = JWT.create()
                        .withSubject(account.getUserId())
                        .withExpiresAt(new Date(System.currentTimeMillis() + Constants.JWT_EXPIRE_ACCESS))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim(Constants.JWT_CLAIMS_ROLE, decodedJWT.getClaim(Constants.JWT_CLAIMS_ROLE).asList(String.class))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", accessToken);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            }catch (Exception exception) {
                response.setHeader("error", exception.getMessage());
                response.setStatus(UNAUTHORIZED.value());
                //response.sendError(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_type", "REFRESH_TOKEN_FAILED");
                error.put("error_message", exception.getMessage());
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}

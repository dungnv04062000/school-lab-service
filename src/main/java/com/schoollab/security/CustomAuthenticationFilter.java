package com.schoollab.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import com.schoollab.common.constants.Constants;
import com.schoollab.common.error.BadRequestException;
import com.schoollab.controller.response.ResponseCodeEnum;
import com.schoollab.common.util.GoogleIdTokenUtil;
import com.schoollab.controller.request.EmailLoginRequestBody;
import com.schoollab.controller.request.UserLoginRequestBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String requestBody = null;
        try {
            requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(StringUtils.isBlank(requestBody)){
            log.error("Body empty");
            try {
                throw new Exception("Request Body is Empty!");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        ObjectMapper mapper = new ObjectMapper();

        log.info("Request body {}", requestBody);

        UsernamePasswordAuthenticationToken authenticationToken= null;
        if(requestBody.contains("username")) {
            //đăng nhập bằng username và password

            String username = null;
            String password = null;
            try {
                UserLoginRequestBody userLoginRequest = mapper.readValue(requestBody, UserLoginRequestBody.class);
                username = userLoginRequest.getUsername();
                password = userLoginRequest.getPassword();
                if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
                    log.error("Thiếu thông tin khi đăng nhập bằng username-password");
                    throw new BadRequestException("Vui lòng điền đầy đủ thông tin tài khoản");
                }
            } catch (JsonProcessingException e) {
                throw new BadRequestException("Vui lòng điền đầy đủ thông tin tài khoản");
            }

            authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        } else if(requestBody.contains("id_token")) {
            // dang nhap bang google
            String idToken = null;
            try {
                EmailLoginRequestBody gmailLoginRequest = mapper.readValue(requestBody, EmailLoginRequestBody.class);
                idToken = gmailLoginRequest.getIdToken();
                log.info("Id_Token {}", idToken);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            //verifying token
            GoogleIdToken.Payload payload = GoogleIdTokenUtil.verifingToken(idToken);
            if (payload == null) {
                throw new BadRequestException("IdToken không hợp lệ");
            }

            authenticationToken = new UsernamePasswordAuthenticationToken(payload.getEmail(), null);
        }

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException, IOException {
        Algorithm algorithm = Algorithm.HMAC256(Constants.JWT_SECRET_KEY.getBytes());
        authentication.getAuthorities().stream().forEach(a -> System.out.println(a.getAuthority()));
        String accessToken = JWT.create()
                .withSubject((String) authentication.getPrincipal())
                .withExpiresAt(new Date(System.currentTimeMillis() + Constants.JWT_EXPIRE_ACCESS))
                .withIssuer(request.getRequestURL().toString())
                .withClaim(Constants.JWT_CLAIMS_ROLE, authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withSubject((String) authentication.getPrincipal())
                .withExpiresAt(new Date(System.currentTimeMillis() + Constants.JWT_EXPIRE_REFRESH))
                .withIssuer(request.getRequestURL().toString())
                .withClaim(Constants.JWT_CLAIMS_ROLE, authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        /*response.setHeader("access_token", access_token);
        response.setHeader("refresh_token", refresh_token);*/
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        ResponseBodyDto<Map> responseBodyDto = new ResponseBodyDto<>(tokens, ResponseCodeEnum.R_200, "OK");
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        Map<String, String> errors = new HashMap<>();
        errors.put("error_code", ResponseCodeEnum.R_400.name());
        errors.put("error_message", "Thông tin không chính xác");
        new ObjectMapper().writeValue(response.getOutputStream(), errors);
    }
}
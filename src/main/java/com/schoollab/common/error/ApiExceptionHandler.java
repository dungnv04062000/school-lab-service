package com.schoollab.common.error;

import com.schoollab.controller.response.ResponseBodyDto;
import com.schoollab.controller.response.ResponseCodeEnum;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

//	@ExceptionHandler(Exception.class)
//	public @ResponseBody ResponseEntity<ResponseBodyDto<Object>> handleServerError(Exception ex, WebRequest request) {
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		ResponseBodyDto<Object> dtoResult = new ResponseBodyDto<>();
//		dtoResult.setCode(ResponseCodeEnum.E_0001);
//		dtoResult.setMessage("Error unknown");
//		System.out.println(ex);
//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).body(dtoResult);
//	}

	@ExceptionHandler(BadRequestException.class)
	public @ResponseBody ResponseEntity<ResponseBodyDto<Object>> handleBadRequestException(Exception ex,
			WebRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseBodyDto<Object> dtoResult = new ResponseBodyDto<Object>();
		dtoResult.setCode(ResponseCodeEnum.R_400);
		dtoResult.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(dtoResult);
	}

	@ExceptionHandler(NotFoundException.class)
	public @ResponseBody ResponseEntity<ResponseBodyDto<Object>> handleNotFoundException(Exception ex) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseBodyDto<Object> dtoResult = new ResponseBodyDto<>();
		dtoResult.setCode(ResponseCodeEnum.R_404);
		dtoResult.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).body(dtoResult);
	}

	@ExceptionHandler(ForbiddenException.class)
	public @ResponseBody ResponseEntity<ResponseBodyDto<Object>> handleForbiddenException(Exception ex) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseBodyDto<Object> dtoResult = new ResponseBodyDto<>();
		dtoResult.setCode(ResponseCodeEnum.R_403);
		dtoResult.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(headers).body(dtoResult);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public @ResponseBody ResponseEntity<ResponseBodyDto<Object>> handleNotValidException(MethodArgumentNotValidException ex) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseBodyDto<Object> dtoResult = new ResponseBodyDto<>();
		dtoResult.setCode(ResponseCodeEnum.R_400);
		dtoResult.setMessage(ex.getFieldError().getDefaultMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(dtoResult);
	}
}
